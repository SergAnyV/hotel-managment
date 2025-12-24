package com.asv.services.impl;

import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import com.asv.feign.HotelClientFeign;
import com.asv.models.roomdto.RoomDTO;
import com.asv.repositories.ChatEntityRepository;
import com.asv.services.AbstractProcessHandler;
import com.asv.services.BotSenderMessageService;
import com.asv.services.ProcessHandler;
import com.asv.services.StringText;
import com.asv.sessions.UserSession;
import com.asv.ui.MenuButtonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.asv.services.StringText.*;

/**
 * Обработчик процесса получения информации о номере отеля по его номеру.
 * Реализует одностадийную логику: запрос номера комнаты → обращение к сервису отеля →
 * форматирование и отправка информации пользователю или сообщение об отсутствии номера.
 * После завершения сессия сбрасывается.
 */
@Slf4j
@Service
public class GetRoomInfoProcessHandler extends AbstractProcessHandler implements ProcessHandler {

    private final HotelClientFeign hotelClientFeign;
    private final ChatEntityRepository chatEntityRepository;

    public GetRoomInfoProcessHandler(
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
     * @return {@code true}, если тип процесса — {@link ProcessType#GET_ROOM_INFO}; {@code false} в противном случае
     */
    @Override
    public boolean canHandle(ProcessType processType) {
        return processType == ProcessType.GET_ROOM_INFO;
    }

    /**
     * Инициирует процесс получения информации о номере: устанавливает начальное состояние сессии
     * и запрашивает у пользователя номер комнаты.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя
     */
    @Override
    public void startProcess(long chatId, UserSession session) {
        session.startProcess(ProcessType.GET_ROOM_INFO);
        sendMessageWithBackButton(chatId, StringText.ENTER_ROOM_N);
        log.info("Начат процесс получения информации о комнате для пользователя {}", chatId);
    }

    /**
     * Обрабатывает единственный шаг процесса — ввод номера комнаты.
     * При неизвестном состоянии сессия сбрасывается, и отправляется сообщение об ошибке.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput ввод пользователя — номер комнаты
     * @param session   сессия пользователя
     * @param messageId идентификатор входящего сообщения (в данном обработчике не используется для удаления)
     */
    @Override
    public void handleStep(long chatId, String userInput, UserSession session, int messageId) {
        ProcessState currentState = session.getProcessState();

        if (currentState == ProcessState.GET_ROOM_INFO_ENTERING_NUMBER) {
            handleRoomNumber(chatId, userInput, session);
        } else {
            log.warn("Неизвестное состояние для GET_ROOM_INFO: {}", currentState);
            session.resetProcess();
            sendMessageWithMenu(chatId, PLEASE_ENTER_WRIGHT_DATA + NOT_AVAILABLE + AVAILABLE_ROOMS, isUserAuthenticated(chatId));
        }

        updateSessionActivity(session);
    }

    /**
     * Обрабатывает введённый пользователем номер комнаты:
     * нормализует строку, проверяет на пустоту, выполняет запрос к сервису отеля.
     * В случае успеха — отправляет форматированную информацию о номере.
     * В случае отсутствия — уведомляет об этом.
     * После обработки сессия сбрасывается.
     *
     * @param chatId     уникальный идентификатор чата в Telegram
     * @param roomNumber номер комнаты, введённый пользователем
     * @param session    сессия пользователя
     */
    private void handleRoomNumber(long chatId, String roomNumber, UserSession session) {
        String normalizedRoomNumber = normalizeInutLine(roomNumber);

        if (normalizedRoomNumber.isBlank()) {
            sendMessageWithMenu(chatId, PLEASE_ENTER_WRIGHT_DATA + NOT_AVAILABLE + AVAILABLE_ROOMS, isUserAuthenticated(chatId));
            return;
        }

        try {
            ResponseEntity<RoomDTO> response = hotelClientFeign.getRoomByNumber(normalizedRoomNumber);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                RoomDTO room = response.getBody();
                String message = formatRoomInfo(room);
                sendMessageWithMenu(chatId, message, isUserAuthenticated(chatId));
                log.info("Информация о комнате {} отправлена пользователю chatId, {}", roomNumber, chatId);
            } else {
                sendMessageWithMenu(chatId, PLEASE_ENTER_WRIGHT_DATA + NOT_AVAILABLE + AVAILABLE_ROOMS, isUserAuthenticated(chatId));
                log.warn("Комната {} не найдена для пользователя chatId, {}", normalizedRoomNumber, chatId);
            }
        } catch (Exception e) {
            sendMessageWithMenu(chatId, PLEASE_ENTER_WRIGHT_DATA + NOT_AVAILABLE + AVAILABLE_ROOMS, isUserAuthenticated(chatId));
            log.error("Ошибка при получении информации о комнате {} для пользователя chatId, {}", normalizedRoomNumber, chatId, e);
        }finally {
            session.resetProcessTypeAndState();
        }

    }

    private String normalizeInutLine(String input) {
        return input.replaceAll("[^\\p{L}\\p{N}]", "").toLowerCase();
    }

    /**
     * Форматирует информацию о номере отеля в читаемый вид для отправки пользователю.
     *
     * @param room объект с данными о номере
     * @return отформатированная строка с номером, типом, вместимостью и ценой за ночь
     */
    private String formatRoomInfo(RoomDTO room) {
        return StringText.ROOM + StringText.DOUBLE_POINT + room.getNumber() + StringText.NEXT_LINE
                + StringText.TYPE + StringText.DOUBLE_POINT + room.getDescription() + StringText.NEXT_LINE
                + StringText.MAX_CAPACITY_LIVING + StringText.DOUBLE_POINT + room.getCapacity() + StringText.NEXT_LINE
                + StringText.PRICE + StringText.DOUBLE_POINT + room.getPricePerNight() + StringText.MONEY;
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
