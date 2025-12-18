package com.asv.services.impl;

import com.asv.services.AbstractProcessHandler;
import com.asv.services.ProcessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import com.asv.feign.HotelClientFeign;
import com.asv.repositories.ChatEntityRepository;
import com.asv.services.BotSenderMessageService;
import com.asv.sessions.UserSession;
import com.asv.ui.MenuButtonService;
import org.springframework.http.ResponseEntity;


import static com.asv.services.StringText.*;

/**
 * Обработчик процесса удаления бронирования по его идентификатору.
 * Реализует одностадийную логику: запрос номера бронирования → отправка запроса на удаление
 * в сервис отеля → уведомление пользователя об успехе или ошибке.
 * После завершения сессия сбрасывается.
 */
@Slf4j
@Service
public class DeleteBookingProcessHandler extends AbstractProcessHandler implements ProcessHandler {
    private final HotelClientFeign hotelClientFeign;
    private final ChatEntityRepository chatEntityRepository;


    public DeleteBookingProcessHandler(
            BotSenderMessageService sender,
            ChatEntityRepository chatEntityRepository,
            MenuButtonService menuButtonService,
            HotelClientFeign hotelClientFeign) {
        super(sender, menuButtonService);
        this.hotelClientFeign = hotelClientFeign;
        this.chatEntityRepository = chatEntityRepository;
    }

    /**
     * Проверяет, поддерживает ли данный обработчик указанный тип процесса.
     *
     * @param processType тип процесса
     * @return {@code true}, если тип процесса — {@link ProcessType#DELETE_BOOKING}; {@code false} в противном случае
     */
    @Override
    public boolean canHandle(ProcessType processType) {
        return processType == ProcessType.DELETE_BOOKING;
    }

    /**
     * Инициирует процесс удаления бронирования: устанавливает начальное состояние сессии
     * и запрашивает у пользователя идентификатор бронирования.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя
     */
    @Override
    public void startProcess(long chatId, UserSession session) {
        session.startProcess(ProcessType.DELETE_BOOKING);
        sendMessageWithBackButton(chatId, ENTER_BOOKING_NUMBER);
        log.info("Начат процесс удаления информации о бронирование для пользователя {}", chatId);
    }

    /**
     * Обрабатывает единственный шаг процесса — ввод идентификатора бронирования.
     * При неизвестном состоянии сессия сбрасывается, и отправляется сообщение об ошибке.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput ввод пользователя — номер бронирования (ожидается число)
     * @param session   сессия пользователя
     * @param messageId идентификатор входящего сообщения (в данном обработчике не используется для удаления)
     */
    @Override
    public void handleStep(long chatId, String userInput, UserSession session, int messageId) {
        ProcessState currentState = session.getProcessState();

        if (currentState == ProcessState.DELETE_BOOKING_ENTERING_ID) {
            handleBookingId(chatId, userInput, session);
        } else {
            log.warn("Неизвестное состояние для DELETE_BOOKING: {}", currentState);
            session.resetProcessTypeAndState();
            sendMessageWithMenu(chatId, PROBLEM_TRY_AGAIN_LATER, false);
        }

        updateSessionActivity(session);
    }

    /**
     * Обрабатывает введённый пользователем идентификатор бронирования:
     * проверяет, что ввод содержит число, отправляет запрос на удаление в сервис отеля.
     * В случае успешного удаления — уведомляет об этом.
     * В случае ошибки (некорректный ID, бронь не найдена, сетевая ошибка) —
     * уведомляет о невозможности удаления.
     * После обработки сессия сбрасывается.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param bookingId строковое представление идентификатора бронирования
     * @param session   сессия пользователя
     */
    private void handleBookingId(long chatId, String bookingId, UserSession session) {
        String normalizedBookingId = bookingId.strip();

        if (normalizedBookingId.isBlank()) {
            sendMessageWithBackButton(chatId, PLEASE_ENTER_WRIGHT_DATA);
            return;
        }

        try {
            Long bookingIdL = Long.parseLong(normalizedBookingId);
            log.info(bookingIdL.toString());
            ResponseEntity<Void> response = hotelClientFeign.deleteById(normalizedBookingId);
            if (response.getStatusCode().is2xxSuccessful()) {
                sendMessageWithMenu(chatId, BOOKING_REMOVED, isUserAuthenticated(chatId));
                log.info("Удаление брони bookingId {} успешно для chatId, {}", bookingId, chatId);
            } else {
                sendMessageWithMenu(chatId, CANT_BOOKING_REMOVED,
                        isUserAuthenticated(chatId));
                log.warn("Бронь {} не найдена для пользователя chatId, {}", normalizedBookingId, chatId);
                session.resetProcessTypeAndState();
            }
        } catch (Exception e) {
            sendMessageWithMenu(chatId, CANT_BOOKING_REMOVED, isUserAuthenticated(chatId));
            log.error("Ошибка при удаление информации о броне {} для пользователя chatId, {}", normalizedBookingId, chatId, e);
            session.resetProcessTypeAndState();
        }

        session.resetProcessTypeAndState();
    }

    /**
     * Проверяет, аутентифицирован ли пользователь по наличию JWT-токена, привязанного к chatId.
     *
     * @param chatId уникальный идентификатор чата в Telegram
     * @return {@code true}, если токен найден; {@code false} в противном случае
     */
    private boolean isUserAuthenticated(long chatId) {
        return chatEntityRepository.findTokenByChatId(chatId).isPresent();
    }
}
