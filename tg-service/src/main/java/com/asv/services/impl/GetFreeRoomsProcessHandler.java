package com.asv.services.impl;

import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import com.asv.feign.HotelClientFeign;
import com.asv.models.roomdto.RoomSimpleDataBaseDTO;
import com.asv.repositories.ChatEntityRepository;
import com.asv.services.AbstractProcessHandler;
import com.asv.services.BotSenderMessageService;
import com.asv.services.ProcessHandler;
import com.asv.sessions.UserSession;
import com.asv.ui.MenuButtonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static com.asv.services.StringText.*;

/**
 * Обработчик процесса поиска свободных номеров отеля на заданный период.
 * Реализует двухэтапную логику:
 * 1. Ввод даты заезда (должна быть сегодня или позже);
 * 2. Ввод даты выезда (должна быть строго после даты заезда).
 * После валидации дат выполняется запрос к сервису отеля и отправляется
 * форматированный список доступных номеров или сообщение об их отсутствии.
 */
@Slf4j
@Service
public class GetFreeRoomsProcessHandler extends AbstractProcessHandler implements ProcessHandler {
    private final HotelClientFeign hotelClientFeign;
    private final ChatEntityRepository chatEntityRepository;
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public GetFreeRoomsProcessHandler(
            BotSenderMessageService sender,
            MenuButtonService menuButtonService,
            ChatEntityRepository chatEntityRepository,
            HotelClientFeign hotelClientFeign) {
        super(sender, menuButtonService);
        this.hotelClientFeign = hotelClientFeign;
        this.chatEntityRepository = chatEntityRepository;
    }

    /**
     * Проверяет, поддерживает ли данный обработчик указанный тип процесса.
     *
     * @param processType тип процесса
     * @return {@code true}, если тип процесса — {@link ProcessType#GET_FREE_ROOMS}; {@code false} в противном случае
     */
    @Override
    public boolean canHandle(ProcessType processType) {
        return processType == ProcessType.GET_FREE_ROOMS;
    }

    /**
     * Инициирует процесс поиска свободных номеров: устанавливает начальное состояние сессии
     * и запрашивает у пользователя дату заезда.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя
     */
    @Override
    public void startProcess(long chatId, UserSession session) {
        session.startProcess(ProcessType.GET_FREE_ROOMS);
        sendMessageWithBackButton(chatId, ENTER_BOOKING_CHECK_IN_DATES);
        log.info("Начат процесс поиска свободных комнат для пользователя chatId {}", chatId);
    }

    /**
     * Обрабатывает текущий шаг процесса в зависимости от состояния сессии:
     * либо ввод даты заезда, либо ввод даты выезда.
     * При неизвестном состоянии сессия сбрасывается, и отправляется сообщение об ошибке.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput ввод пользователя — дата в формате "ДД.ММ.ГГГГ"
     * @param session   сессия пользователя
     * @param messageId идентификатор входящего сообщения (в данном обработчике не используется для удаления)
     */
    @Override
    public void handleStep(long chatId, String userInput, UserSession session, int messageId) {
        ProcessState currentState = session.getProcessState();

        switch (currentState) {
            case GET_FREE_ROOMS_ENTERING_CHECK_IN_DATE -> handleCheckInDate(chatId, userInput, session);
            case GET_FREE_ROOMS_ENTERING_CHECK_OUT_DATE -> handleCheckOutDate(chatId, userInput, session);
            default -> {
                log.warn("Неизвестное состояние для GET_FREE_ROOMS: {}", currentState);
                session.resetProcessTypeAndState();
                sendMessageWithMenu(chatId, PROBLEM_TRY_AGAIN_LATER, false);
            }
        }

        updateSessionActivity(session);
    }

    /**
     * Обрабатывает введённую пользователем дату заезда:
     * парсит, проверяет формат и логическую валидность (не ранее текущей даты).
     * При успехе сохраняет дату и запрашивает дату выезда.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param dateInput строка с датой заезда в формате "ДД.ММ.ГГГГ"
     * @param session   сессия пользователя
     */
    private void handleCheckInDate(long chatId, String dateInput, UserSession session) {
        Optional<LocalDate> parsedDate = parseDate(dateInput);
        log.info(parsedDate.toString());
        if (parsedDate.isEmpty()) {
            sendMessageWithBackButton(chatId, INCORRECT_DATE_FORMATE);
            return;
        }

        LocalDate checkInDate = parsedDate.get();
        LocalDate today = LocalDate.now();

        if (checkInDate.isBefore(today)) {
            sendMessageWithBackButton(chatId, PLEASE_ENTER_WRIGHT_DATA_DATES);
            return;
        }

        session.saveDataAndContinue(CHECK_IN_KEY, checkInDate);
        sendMessageWithBackButton(chatId, ENTER_BOOKING_CHECK_OUT_DATES);
        log.debug("Пользователь {} ввел дату заезда: {}", chatId, checkInDate.format(INPUT_DATE_FORMAT));
    }

    /**
     * Обрабатывает введённую пользователем дату выезда:
     * парсит, проверяет формат и логическую валидность (строго после даты заезда и не сегодня/в прошлом).
     * При успехе запускает поиск свободных номеров.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param dateInput строка с датой выезда в формате "ДД.ММ.ГГГГ"
     * @param session   сессия пользователя
     */
    private void handleCheckOutDate(long chatId, String dateInput, UserSession session) {
        Optional<LocalDate> parsedDate = parseDate(dateInput);

        if (parsedDate.isEmpty()) {
            sendMessageWithBackButton(chatId, PLEASE_ENTER_WRIGHT_DATA_DATES);
            return;
        }

        LocalDate checkOutDate = parsedDate.get();
        LocalDate checkInDate = session.getData(CHECK_IN_KEY, LocalDate.class);
        LocalDate today = LocalDate.now();

        if (checkOutDate.isBefore(today) || checkOutDate.isEqual(today)) {
            sendMessageWithBackButton(chatId, PLEASE_ENTER_WRIGHT_DATA_DATES);
            return;
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            sendMessageWithBackButton(chatId, PLEASE_ENTER_WRIGHT_DATA_DATES);
            return;
        }

        session.saveDataAndContinue(CHECK_OUT_KEY, checkOutDate);

        searchFreeRooms(chatId, session);
        session.resetProcessTypeAndState();
    }

    /**
     * Выполняет запрос к сервису отеля для получения списка свободных номеров
     * на указанный период и отправляет результат пользователю.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя, содержащая валидные даты заезда и выезда
     */
    private void searchFreeRooms(long chatId, UserSession session) {
        LocalDate checkInDate = session.getData(CHECK_IN_KEY, LocalDate.class);
        LocalDate checkOutDate = session.getData(CHECK_OUT_KEY, LocalDate.class);

        log.info("Поиск свободных комнат с {} по {} для пользователя {}",
                checkInDate, checkOutDate, chatId);
        String checkInStr = checkInDate.toString();
        String checkOutStr = checkOutDate.toString();


        try {

            ResponseEntity<List<RoomSimpleDataBaseDTO>> response =
                    hotelClientFeign.getFreeRoomsBetweenDates(checkInStr, checkOutStr);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<RoomSimpleDataBaseDTO> freeRooms = response.getBody();

                if (freeRooms.isEmpty()) {
                    sendMessageWithMenu(chatId,
                            NOT_FOUND_FREE_ROOMS,
                            isUserAuthenticated(chatId));
                    log.info("Свободных комнат не найдено для пользователя chatId {}", chatId);
                } else {
                    String message = formatFreeRoomsResponse(freeRooms, checkInDate, checkOutDate);
                    sendMessageWithMenu(chatId, message, isUserAuthenticated(chatId));
                    log.info("Найдено {} свободных комнат для пользователя {}", freeRooms.size(), chatId);
                }
            } else {
                sendMessageWithMenu(chatId,
                        PROBLEM_TRY_AGAIN_LATER,
                        isUserAuthenticated(chatId));
                log.warn("Ошибка ответа от сервера при поиске свободных комнат для пользователя chatId {}", chatId);
            }
        } catch (Exception e) {
            sendMessageWithMenu(chatId,
                    PROBLEM_TRY_AGAIN_LATER,
                    isUserAuthenticated(chatId));
            log.error("Ошибка при поиске свободных комнат для пользователя chatId {}", chatId, e);
        }
    }

    /**
     * Форматирует список доступных номеров в читаемое сообщение для пользователя.
     *
     * @param rooms    список DTO свободных номеров
     * @param checkIn  дата заезда
     * @param checkOut дата выезда
     * @return отформатированная строка с информацией о доступных номерах
     */
    private String formatFreeRoomsResponse(List<RoomSimpleDataBaseDTO> rooms,
                                           LocalDate checkIn, LocalDate checkOut) {
        StringBuilder sb = new StringBuilder();

        sb.append(ACCESSIBLE_ROOM).append(rooms.size()).append(NEXT_LINE);
        sb.append(CHECK_IN_UP).append(checkIn.format(INPUT_DATE_FORMAT))
                .append(" - ").append(checkOut.format(INPUT_DATE_FORMAT)).append(NEXT_LINE).append(NEXT_LINE);

        for (int i = 0; i < rooms.size(); i++) {
            RoomSimpleDataBaseDTO room = rooms.get(i);
            sb.append(i + 1).append(SPACE).append(SPACE).append(ROOM).append(room.getNumber()).append(NEXT_LINE);
            sb.append(room.getDescription()).append(NEXT_LINE);
            sb.append(TYPE).append(DOUBLE_POINT).append(room.getType()).append(NEXT_LINE);
            sb.append(MAX_CAPACITY_LIVING).append(DOUBLE_POINT).append(room.getCapacity()).append(NEXT_LINE);
            sb.append(PRICE).append(DOUBLE_POINT).append(String.format("%.2f", room.getPricePerNight())).append(MONEY).append(NEXT_LINE).append(NEXT_LINE);

            if (i < rooms.size() - 1) {
                sb.append(NEXT_LINE);
            }
        }

        return sb.toString();
    }

    /**
     * Парсинг даты из строки формата ДД.ММ.ГГГГ.
     */
    private Optional<LocalDate> parseDate(String dateStr) {
        try {

            dateStr = dateStr.strip();


            LocalDate date = LocalDate.parse(dateStr, INPUT_DATE_FORMAT);
            return Optional.of(date);
        } catch (DateTimeParseException e) {
            log.debug("Ошибка парсинга даты: {}", dateStr, e);
            return Optional.empty();
        }
    }

    private boolean isUserAuthenticated(long chatId) {

        return chatEntityRepository.findTokenByChatId(chatId).isPresent();
    }
}
