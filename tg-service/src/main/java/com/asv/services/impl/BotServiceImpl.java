package com.asv.services.impl;


import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import com.asv.feign.ChatContext;
import com.asv.repositories.ChatEntityRepository;
import com.asv.services.BotService;
import com.asv.services.ProcessHandler;
import com.asv.services.StringText;
import com.asv.sessions.UserSession;
import com.asv.sessions.UserSessionStorage;
import com.asv.ui.KeyboardVariables;

import com.asv.services.BotSenderMessageService;
import com.asv.feign.HotelClientFeign;
import com.asv.ui.MenuButtonService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.asv.services.StringText.PLEASE_USE_MENU;


@Slf4j
@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {
    private final ProcessHandlerFactory processHandlerFactory;
    private final BotSenderMessageService sender;
    private final MenuButtonService menuButtonService;
    private final HotelClientFeign hotelClientFeign;
    private final UserSessionStorage sessionStorage;
    private final ChatEntityRepository chatEntityRepository;
    private final RateLimiter rateLimiter;

    @Override
    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            long chatId = msg.getChatId();
            int messageId = msg.getMessageId();

            if (!rateLimiter.allow(chatId)) {
                sender.sendMessage(chatId, "Вы отправляете слишком много сообщений. Подождите немного.");
                return;
            }

            String text = msg.getText().strip();
            String username = msg.getFrom().getUserName();
            log.info("User {} chat Id {} text {}", username, chatId, text);

            ChatContext.setChatId(chatId);

            try {
                if (StringText.START_BOT.equals(text)) {
                    sendWelcome(chatId, username);
                } else {
                    if (!handleActiveProcess(chatId, text, messageId)) {
                        handleMenuChoice(chatId, text);
                    }
                }
            } finally {
                ChatContext.clear();
            }
        }
    }

    /**
     * Обрабатывает сообщение как часть активного многошагового процесса.
     */
    private boolean handleActiveProcess(long chatId, String text, int messageId) {
        UserSession session = sessionStorage.getOrCreate(chatId);
        ProcessType type = session.getProcessType();

        if (type == ProcessType.NONE || KeyboardVariables.BACK_TO_MAIN_MENU_BUTTON.equals(text)) {
            return false;
        }

        if (processHandlerFactory.hasHandler(type)) {
            ProcessHandler handler = processHandlerFactory.getHandler(type);
            handler.handleStep(chatId, text, session, messageId);
            return true;
        } else {
            log.warn("Не найден обработчик для типа процесса: {}", type);
            session.resetProcess();
            return false;
        }
    }

    /**
     * Основной метод обработки меню - определяет тип меню и делегирует обработку
     */
    private void handleMenuChoice(long chatId, String text) {
        boolean isAuthenticated = isAuthenticatedChat(chatId);

        if (isAuthenticated) {
            handleAuthenticatedMenu(chatId, text);
        } else {
            handlePublicMenu(chatId, text);
        }
    }

    /**
     * Обработка меню для аутентифицированных пользователей
     */
    private void handleAuthenticatedMenu(long chatId, String text) {
        UserSession session = sessionStorage.getOrCreate(chatId);

        switch (text) {
            case KeyboardVariables.EXIT_BUTTON -> {
                session.resetProcessTypeAndState();
                chatEntityRepository.deleteById(chatId);
                sender.sendMessage(chatId, StringText.MAIN_MENU, menuButtonService.startMenuButtons());
            }
            case KeyboardVariables.BACK_TO_MAIN_MENU_BUTTON -> {
                sessionStorage.remove(chatId);
                sender.sendMessage(chatId, StringText.MAIN_MENU, menuButtonService.authMenuButtons());
            }
            case KeyboardVariables.VIEW_ROOMS_BUTTON -> {
                sendPublicInfo(hotelClientFeign::getAllRooms,
                        rooms -> rooms.stream().map(room ->
                                "Комната: " + room.getNumber() + "\n" + "Данные: " + room.getDescription() + "\n" +
                                        "Тип комнаты: " + room.getType() + "\n" +
                                        "Максимальное количество проживающих: " + room.getCapacity() + "\n" +
                                        "цена :" + room.getPricePerNight() + " р." + "\n"
                        ).collect(Collectors.joining("\n")),
                        chatId
                );
            }
            case KeyboardVariables.VIEW_ADDITION_SERVICES_BUTTON -> {
                sendPublicInfo(hotelClientFeign::getAll,
                        services -> services.stream().map(service ->
                                "Сервис " + service.getTitle() + " - " + "\n" + service.getDescription() + "\n" +
                                        "цена :" + service.getPrice() + " р." + "\n"
                        ).collect(Collectors.joining("\n")),
                        chatId
                );
            }
            case KeyboardVariables.GET_FREE_ROOMS_FOR_DATES_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.GET_FREE_ROOMS);
                handler.startProcess(chatId, session);
            }
            case KeyboardVariables.GET_INFO_ROOM_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.GET_ROOM_INFO);
                handler.startProcess(chatId, session);
            }
            case KeyboardVariables.GET_INFO_SERVICE_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.GET_SERVICE_INFO);
                handler.startProcess(chatId, session);
            }
            case KeyboardVariables.BOOKING_ROOM_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.BOOKING);
                handler.startProcess(chatId, session);
            }
            case KeyboardVariables.UPDATE_PERSONAL_DATES_BUTTON -> {
                sender.sendMessage(chatId, "Функция обновления личных данных пока не реализована.",
                        menuButtonService.authMenuButtons());
            }
            case KeyboardVariables.VIEW_BOOKING_BY_ID_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.VIEW_BOOKING);
                handler.startProcess(chatId, session);
            }
            case KeyboardVariables.DELETE_BOOKING_BY_ID_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.DELETE_BOOKING);
                handler.startProcess(chatId, session);
            }
            default -> {
                sender.sendMessage(chatId, PLEASE_USE_MENU, menuButtonService.authMenuButtons());
            }
        }
    }

    /**
     * Обработка меню для публичных (неаутентифицированных) пользователей
     */
    private void handlePublicMenu(long chatId, String text) {
        UserSession session = sessionStorage.getOrCreate(chatId);

        switch (text) {
            case KeyboardVariables.EXIT_BUTTON -> {
                session.resetProcessTypeAndState();
                chatEntityRepository.deleteById(chatId);
                sender.sendMessage(chatId, StringText.MAIN_MENU, menuButtonService.startMenuButtons());

            }
            case KeyboardVariables.BACK_TO_MAIN_MENU_BUTTON -> {
                sessionStorage.remove(chatId);
                sender.sendMessage(chatId, StringText.MAIN_MENU, menuButtonService.startMenuButtons());

            }
            case KeyboardVariables.VIEW_ROOMS_BUTTON -> {
                sendPublicInfo(hotelClientFeign::getAllRooms,
                        rooms -> rooms.stream().map(room ->
                                "Комната: " + room.getNumber() + "\n" + "Данные: " + room.getDescription() + "\n" +
                                        "Тип комнаты: " + room.getType() + "\n" +
                                        "Максимальное количество проживающих: " + room.getCapacity() + "\n" +
                                        "цена :" + room.getPricePerNight() + " р." + "\n"
                        ).collect(Collectors.joining("\n")),
                        chatId
                );

            }
            case KeyboardVariables.VIEW_ADDITION_SERVICES_BUTTON -> {
                sendPublicInfo(hotelClientFeign::getAll,
                        services -> services.stream().map(service ->
                                "Сервис " + service.getTitle() + " - " + "\n" + service.getDescription() + "\n" +
                                        "цена :" + service.getPrice() + " р." + "\n"
                        ).collect(Collectors.joining("\n")),
                        chatId
                );

            }
            case KeyboardVariables.LOGIN_PASSWORD_BUTTON -> {
                session.setProcessType(ProcessType.SIGN_IN);
                session.setProcessState(ProcessState.SIGN_IN_ENTERING_LOGIN);
                sender.sendMessage(chatId, "Введите логин:", menuButtonService.backMenuButton());

            }
            case KeyboardVariables.REGISTRATION_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.USER_REGISTRATION);
                handler.startProcess(chatId, session);

            }
            case KeyboardVariables.GET_FREE_ROOMS_FOR_DATES_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.GET_FREE_ROOMS);
                handler.startProcess(chatId, session);

            }
            case KeyboardVariables.GET_INFO_ROOM_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.GET_ROOM_INFO);
                handler.startProcess(chatId, session);

            }
            case KeyboardVariables.GET_INFO_SERVICE_BUTTON -> {
                ProcessHandler handler = processHandlerFactory.getHandler(ProcessType.GET_SERVICE_INFO);
                handler.startProcess(chatId, session);

            }
            default -> {
                sender.sendMessage(chatId, PLEASE_USE_MENU, menuButtonService.startMenuButtons());
            }
        }
    }

    private boolean isAuthenticatedChat(long chatId) {
        return chatEntityRepository.findTokenByChatId(chatId).isPresent();
    }

    private <T> void sendPublicInfo(
            Supplier<ResponseEntity<List<T>>> supplier,
            Function<List<T>, String> mapper,
            long chatId
    ) {
        ResponseEntity<List<T>> response = supplier.get();
        boolean isAuthenticated = chatEntityRepository.findTokenByChatId(chatId).isPresent();
        var menu = isAuthenticated
                ? menuButtonService.authMenuButtons()
                : menuButtonService.startMenuButtons();

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String messageText = mapper.apply(response.getBody());
            sender.sendMessage(chatId, messageText, menu);
        } else {
            sender.sendMessage(chatId,
                    "Не удалось получить данные, попробуйте позже — мы уже пытаемся починить",
                    menu
            );
        }
    }

    /**
     * Отправка приветственного сообщения.
     */
    private void sendWelcome(long chatId, String username) {

        log.info("новый пользователь username  {}  chatId {}", username, chatId);

        if (username == null || username.isBlank()) {
            username = "Друг";
        }
        String text = "Добро пожаловать, " + username + "! Выберите из раздела меню, интересующую вас информацию." + "\n"
                + "Это демонстрационный бот, вы можете попробовать разные варианты в том числе и войти под логином" +
                " и паролем, для прохождения авторизации вам нужно будет пройти по ссылке в письме, но в данной " +
                "демонстрационной модели она не работает, которое мы отправили ," +
                " для подтверждения почты." + "\n" + "\n" + "Для бронирования необходимо войти под своим логином и паролем." +
                " В качестве примера можете использовать: Логин- admin_ivan , Пароль- password";
        sender.sendMessage(chatId, text, menuButtonService.startMenuButtons());
    }

}

