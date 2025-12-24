package com.asv.services.impl;

import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import com.asv.feign.HotelClientFeign;
import com.asv.models.security.SignInRequest;
import com.asv.services.AbstractProcessHandler;
import com.asv.services.BotSenderMessageService;
import com.asv.services.ChatEntityService;
import com.asv.services.ProcessHandler;
import com.asv.sessions.UserSession;
import com.asv.ui.MenuButtonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.asv.services.StringText.*;

/**
 * Обработчик процесса аутентификации пользователя (входа в систему).
 * Реализует пошаговую логику:
 * 1. Запрос логина (никнейма);
 * 2. Запрос пароля;
 * 3. Отправка данных на сервер аутентификации;
 * 4. Сохранение JWT-токена в привязке к chatId при успешной аутентификации.
 * <p>
 * После завершения (успешного или нет) сессия сбрасывается.
 */
@Slf4j
@Service
public class SignInProcessHandler extends AbstractProcessHandler implements ProcessHandler {

    private final HotelClientFeign hotelClientFeign;
    private final ChatEntityService chatEntityService;

    public SignInProcessHandler(
            BotSenderMessageService sender,
            MenuButtonService menuButtonService,
            HotelClientFeign hotelClientFeign,
            ChatEntityService chatEntityService) {
        super(sender, menuButtonService);
        this.hotelClientFeign = hotelClientFeign;
        this.chatEntityService = chatEntityService;
    }

    /**
     * Проверяет, поддерживает ли данный обработчик указанный тип процесса.
     *
     * @param processType тип процесса
     * @return {@code true}, если тип процесса — {@link ProcessType#SIGN_IN}; {@code false} в противном случае
     */
    @Override
    public boolean canHandle(ProcessType processType) {
        return processType == ProcessType.SIGN_IN;
    }

    /**
     * Инициирует процесс входа: устанавливает начальное состояние сессии
     * и запрашивает у пользователя логин (никнейм).
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя
     */
    @Override
    public void startProcess(long chatId, UserSession session) {
        session.startProcess(ProcessType.SIGN_IN);
        sendMessageWithBackButton(chatId, ENTER_NICKNAME);
        log.info("Начат процесс входа для пользователя {}", chatId);
    }

    /**
     * Обрабатывает текущий шаг процесса входа в зависимости от состояния сессии.
     * Поддерживает два шага: ввод логина и ввод пароля.
     * При неизвестном состоянии сессия сбрасывается.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput ввод пользователя (сообщение)
     * @param session   сессия пользователя
     * @param messageId идентификатор входящего сообщения (для последующего удаления)
     */
    @Override
    public void handleStep(long chatId, String userInput, UserSession session, int messageId) {
        ProcessState currentState = session.getProcessState();

        switch (currentState) {
            case SIGN_IN_ENTERING_LOGIN -> handleLoginStep(chatId, userInput, session, messageId);
            case SIGN_IN_ENTERING_PASSWORD -> handlePasswordStep(chatId, userInput, session, messageId);
            default -> {
                log.warn("Неизвестное состояние для процесса входа: {}", currentState);
                session.resetProcessTypeAndState();
            }
        }

        updateSessionActivity(session);
    }

    /**
     * Обрабатывает шаг ввода логина (никнейма).
     * Сохраняет логин во временных данных сессии, удаляет сообщение с паролем
     * (для безопасности), подтверждает приём данных и запрашивает пароль.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param login     введённый пользователем логин
     * @param session   сессия пользователя
     * @param messageId идентификатор сообщения с логином (удаляется для безопасности)
     */
    private void handleLoginStep(long chatId, String login, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        session.saveDataAndContinue(NICKNAME_KEY, login.toLowerCase());
        sendMessageWithBackButton(chatId, ENTER_PASSWORD);
        log.debug("Пользователь {} ввел логин", chatId);
    }

    /**
     * Обрабатывает шаг ввода пароля.
     * Удаляет сообщение с паролем, формирует запрос аутентификации,
     * вызывает сервис проверки учётных данных.
     * При успехе — сохраняет токен и переходит в авторизованное меню.
     * При ошибке — уведомляет пользователя и сбрасывает сессию.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param password  введённый пользователем пароль
     * @param session   сессия пользователя
     * @param messageId идентификатор сообщения с паролем (удаляется для безопасности)
     */
    private void handlePasswordStep(long chatId, String password, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        String login = session.getData(NICKNAME_KEY, String.class);
        SignInRequest request = SignInRequest.builder()
                .nickName(login)
                .password(password.toLowerCase())
                .build();

        try {
            var response = hotelClientFeign.signIn(request);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String token = response.getBody().getAccessToken();
                chatEntityService.creteChatEntity(chatId, token);

                boolean isAuthenticated = true;
                sendMessageWithMenu(chatId, SUCCESSFUL, isAuthenticated);
                log.info("Успешный вход для пользователя {}", chatId);
                session.resetProcessTypeAndState();
            } else {
                boolean isAuthenticated = false;
                sendMessageWithMenu(chatId, PLEASE_ENTER_WRIGHT_DATA, isAuthenticated);
                log.warn("Неудачная попытка входа для пользователя {}", chatId);
                session.resetProcessTypeAndState();
            }
        } catch (Exception e) {
            boolean isAuthenticated = false;
            session.resetProcessTypeAndState();
            sendMessageWithMenu(chatId, PROBLEM_TRY_AGAIN_LATER, isAuthenticated);
            log.error("Ошибка при входе для пользователя {}", chatId, e);
        }

        session.resetProcessTypeAndState();
    }

}
