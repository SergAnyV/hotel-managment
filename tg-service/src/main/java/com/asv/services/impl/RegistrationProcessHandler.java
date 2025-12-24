package com.asv.services.impl;


import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import com.asv.feign.HotelClientFeign;
import com.asv.models.userdto.UserDTO;
import com.asv.services.AbstractProcessHandler;
import com.asv.services.BotSenderMessageService;
import com.asv.services.ProcessHandler;
import com.asv.sessions.UserSession;
import com.asv.ui.MenuButtonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.asv.services.StringText.*;

/**
 * Обработчик процесса регистрации нового пользователя.
 * Реализует пошаговую логику сбора данных:
 * никнейм → имя → отчество → фамилия → email → телефон → пароль.
 * После сбора всех данных отправляет запрос на создание пользователя в сервис отеля.
 * Сообщения с конфиденциальными данными (фамилия, email, телефон, пароль) удаляются
 * после обработки для обеспечения безопасности.
 */
@Slf4j
@Service
public class RegistrationProcessHandler extends AbstractProcessHandler implements ProcessHandler {
    private final HotelClientFeign hotelClientFeign;


    public RegistrationProcessHandler(
            BotSenderMessageService sender,
            MenuButtonService menuButtonService,
            HotelClientFeign hotelClientFeign) {
        super(sender, menuButtonService);
        this.hotelClientFeign = hotelClientFeign;
    }

    /**
     * Проверяет, поддерживает ли данный обработчик указанный тип процесса.
     *
     * @param processType тип процесса
     * @return {@code true}, если тип процесса — {@link ProcessType#USER_REGISTRATION}; {@code false} в противном случае
     */
    @Override
    public boolean canHandle(ProcessType processType) {
        return processType == ProcessType.USER_REGISTRATION;
    }

    /**
     * Инициирует процесс регистрации: устанавливает начальное состояние сессии
     * и запрашивает у пользователя никнейм.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя
     */
    @Override
    public void startProcess(long chatId, UserSession session) {
        session.startProcess(ProcessType.USER_REGISTRATION);
        sendMessageWithBackButton(chatId, ENTER_NICKNAME);
        log.info("Начата регистрация для пользователя chatId  {}", chatId);
    }

    /**
     * Обрабатывает текущий шаг процесса регистрации в зависимости от состояния сессии.
     * Поддерживает семь последовательных шагов ввода персональных данных.
     * При неизвестном состоянии сессия сбрасывается, и пользователь возвращается в главное меню.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput ввод пользователя (сообщение)
     * @param session   сессия пользователя
     * @param messageId идентификатор входящего сообщения (используется для удаления конфиденциальных данных)
     */
    @Override
    public void handleStep(long chatId, String userInput, UserSession session, int messageId) {
        ProcessState currentState = session.getProcessState();

        switch (currentState) {
            case REGISTRATION_ENTERING_NICK_NAME -> handleNickName(chatId, userInput, session);
            case REGISTRATION_ENTERING_NAME -> handleName(chatId, userInput, session);
            case REGISTRATION_ENTERING_FATHERS_NAME -> handleFathersName(chatId, userInput, session);
            case REGISTRATION_ENTERING_FAMILY_NAME -> handleFamilyName(chatId, userInput, session, messageId);
            case REGISTRATION_ENTERING_EMAIL -> handleEmail(chatId, userInput, session, messageId);
            case REGISTRATION_ENTERING_PHONE_NUMBER -> handlePhoneNumber(chatId, userInput, session, messageId);
            case REGISTRATION_ENTERING_PASSWORD -> handlePassword(chatId, userInput, session, messageId);
            default -> {
                log.warn("Неизвестное состояние регистрации: {}", currentState);
                session.resetProcess();
                sendMessageWithMenu(chatId, PLEASE_USE_MENU, false);
            }
        }

        updateSessionActivity(session);
    }

    /**
     * Обрабатывает ввод никнейма и переходит к следующему шагу — запросу имени.
     *
     * @param chatId   уникальный идентификатор чата в Telegram
     * @param nickName введённый пользователем никнейм
     * @param session  сессия пользователя
     */
    private void handleNickName(long chatId, String nickName, UserSession session) {
        session.saveDataAndContinue(NICKNAME_KEY, nickName.toLowerCase());
        sendMessageWithBackButton(chatId, ENTER_NAME);
    }

    /**
     * Обрабатывает ввод имени и переходит к следующему шагу — запросу отчества.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param name    введённое пользователем имя
     * @param session сессия пользователя
     */
    private void handleName(long chatId, String name, UserSession session) {
        session.saveDataAndContinue(NAME_KEY, name);
        sendMessageWithBackButton(chatId, ENTER_FATHER_NAME);
    }

    /**
     * Обрабатывает ввод отчества и переходит к следующему шагу — запросу фамилии.
     *
     * @param chatId      уникальный идентификатор чата в Telegram
     * @param fathersName введённое пользователем отчество
     * @param session     сессия пользователя
     */
    private void handleFathersName(long chatId, String fathersName, UserSession session) {
        session.saveDataAndContinue(FATHERS_NAME_KEY, fathersName);
        sendMessageWithBackButton(chatId, ENTER_FAMILY_NAME);
    }

    /**
     * Обрабатывает ввод фамилии: удаляет сообщение с фамилией (для безопасности),
     * сохраняет данные и переходит к запросу email.
     *
     * @param chatId     уникальный идентификатор чата в Telegram
     * @param familyName введённая пользователем фамилия
     * @param session    сессия пользователя
     * @param messageId  идентификатор сообщения с фамилией (удаляется)
     */
    private void handleFamilyName(long chatId, String familyName, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        session.saveDataAndContinue(FAMILY_KEY, familyName);
        sendMessageWithBackButton(chatId, ENTER_EMAIL);
    }

    /**
     * Обрабатывает ввод email: удаляет сообщение с email (для безопасности),
     * сохраняет данные и переходит к запросу номера телефона.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param email     введённый пользователем email
     * @param session   сессия пользователя
     * @param messageId идентификатор сообщения с email (удаляется)
     */
    private void handleEmail(long chatId, String email, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        session.saveDataAndContinue(EMAIL_KEY, email.toLowerCase());
        sendMessageWithBackButton(chatId, ENTER_PHONE_NUMBER);
    }

    /**
     * Обрабатывает ввод номера телефона: удаляет сообщение с телефоном (для безопасности),
     * сохраняет данные и переходит к запросу пароля.
     *
     * @param chatId      уникальный идентификатор чата в Telegram
     * @param phoneNumber введённый пользователем номер телефона
     * @param session     сессия пользователя
     * @param messageId   идентификатор сообщения с номером телефона (удаляется)
     */
    private void handlePhoneNumber(long chatId, String phoneNumber, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        session.saveDataAndContinue(PHONE_NUMBER_KEY, phoneNumber);
        sendMessageWithBackButton(chatId, ENTER_PASSWORD);
    }

    /**
     * Обрабатывает ввод пароля: удаляет сообщение с паролем (для безопасности),
     * формирует DTO пользователя, отправляет запрос на регистрацию в сервис отеля.
     * В случае успеха — уведомляет об успешной регистрации.
     * В случае ошибки — уведомляет о проблеме.
     * В любом случае сессия сбрасывается после завершения.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param password  введённый пользователем пароль
     * @param session   сессия пользователя
     * @param messageId идентификатор сообщения с паролем (удаляется)
     */
    private void handlePassword(long chatId, String password, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        UserDTO user = UserDTO.builder()
                .nickName(session.getData(NICKNAME_KEY, String.class))
                .firstName(session.getData(NAME_KEY, String.class))
                .fathersName(session.getData(FATHERS_NAME_KEY, String.class))
                .lastName(session.getData(FAMILY_KEY, String.class))
                .email(session.getData(EMAIL_KEY, String.class))
                .phoneNumber(session.getData(PHONE_NUMBER_KEY, String.class))
                .password(password.toLowerCase())
                .type(CLIENT)
                .build();

        try {
            ResponseEntity<UserDTO> response = hotelClientFeign.createUser(user);
            if (response.getStatusCode().is2xxSuccessful()) {
                sendMessageWithMenu(chatId, REGISTRATION_SUCCESSFUL, false);
                log.info("Пользователь {} успешно зарегистрирован", chatId);
            } else {
                sendMessageWithMenu(chatId, SERVER_IS_NOT_AVAILABLE, false);

                log.warn("Ошибка регистрации для пользователя {}", chatId);
            }

        } catch (Exception e) {
            sendMessageWithMenu(chatId, SERVER_IS_NOT_AVAILABLE, false);

            log.error("Ошибка при регистрации для пользователя {}", chatId, e);
        } finally {
            session.resetProcessTypeAndState();
        }
    }


}
