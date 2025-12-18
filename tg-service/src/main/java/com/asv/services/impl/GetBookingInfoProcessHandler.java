package com.asv.services.impl;

import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import com.asv.feign.HotelClientFeign;
import com.asv.models.bookingdto.Guest;
import com.asv.models.bookingdto.ResponseBookingDTO;

import com.asv.models.servicehoteldto.ServiceHotelSimpleDTO;
import com.asv.repositories.ChatEntityRepository;
import com.asv.services.AbstractProcessHandler;
import com.asv.services.BotSenderMessageService;
import com.asv.services.ProcessHandler;
import com.asv.sessions.UserSession;
import com.asv.ui.MenuButtonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

import static com.asv.services.StringText.*;

/**
 * Обработчик процесса просмотра информации о бронировании по его идентификатору.
 * Реализует одностадийную логику: запрос номера бронирования → обращение к сервису отеля →
 * форматирование и отправка полной информации о брони (даты, номер комнаты, гости, сервисы и статус)
 * или сообщение об отсутствии бронирования.
 * После завершения сессия сбрасывается.
 */
@Slf4j
@Service
public class GetBookingInfoProcessHandler extends AbstractProcessHandler implements ProcessHandler {
    private final HotelClientFeign hotelClientFeign;
    private final ChatEntityRepository chatEntityRepository;
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public GetBookingInfoProcessHandler(
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
     * @return {@code true}, если тип процесса — {@link ProcessType#VIEW_BOOKING}; {@code false} в противном случае
     */
    @Override
    public boolean canHandle(ProcessType processType) {
        return processType == ProcessType.VIEW_BOOKING;
    }

    /**
     * Инициирует процесс просмотра бронирования: устанавливает начальное состояние сессии
     * и запрашивает у пользователя идентификатор бронирования.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя
     */
    @Override
    public void startProcess(long chatId, UserSession session) {
        session.startProcess(ProcessType.VIEW_BOOKING);
        sendMessageWithBackButton(chatId, ENTER_BOOKING_NUMBER);
        log.info("Начат процесс получения информации о бронирование для пользователя {}", chatId);
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

        if (currentState == ProcessState.VIEW_BOOKING_ENTERING_ID) {
            handleBookingId(chatId, userInput, session);
        } else {
            log.warn("Неизвестное состояние для VIEW_BOOKING_ENTERING_ID: {}", currentState);
            session.resetProcessTypeAndState();
            sendMessageWithMenu(chatId, PROBLEM_TRY_AGAIN_LATER, false);
            session.resetProcessTypeAndState();
        }

        updateSessionActivity(session);
    }

    /**
     * Обрабатывает введённый пользователем идентификатор бронирования:
     * проверяет, что ввод содержит число, выполняет запрос к сервису отеля.
     * В случае успеха — отправляет полную информацию о бронировании.
     * В случае ошибки (некорректный ID, бронь не найдена, сетевая ошибка) —
     * уведомляет пользователя о проблеме.
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
            ResponseEntity<ResponseBookingDTO> response = hotelClientFeign.getBookingById(normalizedBookingId);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                ResponseBookingDTO booking = response.getBody();
                String message = formatBookingInfo(booking);
                sendMessageWithMenu(chatId, message, isUserAuthenticated(chatId));
                log.info("Информация о броне {} отправлена пользователю chatId, {}", bookingId, chatId);

            } else {

                sendMessageWithMenu(chatId, BOOKING_N_NOT_FOUND,
                        isUserAuthenticated(chatId));
                log.warn("Бронь {} не найдена для пользователя chatId, {}", normalizedBookingId, chatId);

            }
        } catch (Exception e) {
            sendMessageWithMenu(chatId, BOOKING_N_NOT_FOUND, isUserAuthenticated(chatId));
            log.error("Ошибка при получении информации о броне {} для пользователя chatId, {}", normalizedBookingId, chatId, e);
            session.resetProcessTypeAndState();
        }

        session.resetProcessTypeAndState();
    }

    /**
     * Форматирует информацию о бронировании в читаемое сообщение для пользователя,
     * включая даты, номер комнаты, статус, контактный телефон, список дополнительных сервисов
     * и данные о проживающих гостях.
     *
     * @param bookingDTO объект с полной информацией о бронировании
     * @return отформатированная строка с деталями бронирования
     */
    private String formatBookingInfo(ResponseBookingDTO bookingDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append(BOOKING_N).append(bookingDTO.getBookingId()).append(NEXT_LINE)
                .append(CHECK_IN_UP).append(bookingDTO.getCheckInDate().format(INPUT_DATE_FORMAT)).append(NEXT_LINE)
                .append(CHECK_OUT_UP).append(bookingDTO.getCheckOutDate().format(INPUT_DATE_FORMAT)).append(NEXT_LINE)
                .append(TOTAL_PRICE).append(bookingDTO.getTotalPrice().toString()).append(MONEY).append(NEXT_LINE)
                .append(STATUS).append(bookingDTO.getStatusOfBooking().getDescription()).append(NEXT_LINE)
                .append(ROOM).append(bookingDTO.getRoomNumber()).append(NEXT_LINE)
                .append(bookingDTO.getDescriptionTypeOfRoom()).append(NEXT_LINE)
                .append(CONTACT_PHONE_NUMBER).append(bookingDTO.getPhoneNumber()).append(NEXT_LINE);

        if (!bookingDTO.getServiceHotelSimpleDTOS().isEmpty()) {
            sb.append(SERVICES).append(NEXT_LINE);
            for (ServiceHotelSimpleDTO serviceHotelSimpleDTO : bookingDTO.getServiceHotelSimpleDTOS()) {
                sb.append(SERVICE).append(serviceHotelSimpleDTO.getTitle()).append(NEXT_LINE)
                        .append(serviceHotelSimpleDTO.getDescription()).append(NEXT_LINE).append(NEXT_LINE);

            }
        }
        sb.append(LIST_OF_GUESTS).append(NEXT_LINE);
        if (bookingDTO.getGuestList() == null || bookingDTO.getGuestList().isEmpty()) {
            sb.append(SPACE).append(INFORMATION).append(NOT_FOUND);
            return sb.toString();
        }

        for (Guest guest : bookingDTO.getGuestList()) {
            sb.append(NAME).append(DOUBLE_POINT).append(SPACE).append(guest.getName()).append(NEXT_LINE)
                    .append(SURNAME).append(DOUBLE_POINT).append(SPACE).append(guest.getSurname()).append(NEXT_LINE).append(NEXT_LINE);

        }
        return sb.toString();
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
