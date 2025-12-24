package com.asv.services.impl;

import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import com.asv.feign.HotelClientFeign;
import com.asv.models.servicehoteldto.ServiceHotelDTO;
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

/**
 * Обработчик процесса получения информации о дополнительном сервисе отеля по его названию.
 * Реализует одностадийную логику: запрос названия сервиса → обращение к сервису отеля →
 * форматирование и отправка информации пользователю или сообщение об отсутствии сервиса.
 * После завершения сессия сбрасывается.
 */
@Slf4j
@Service
public class GetServiceInfoProcessHandler extends AbstractProcessHandler implements ProcessHandler {
    private final HotelClientFeign hotelClientFeign;
    private final ChatEntityRepository chatEntityRepository;

    public GetServiceInfoProcessHandler(
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
     * @return {@code true}, если тип процесса — {@link ProcessType#GET_SERVICE_INFO}; {@code false} в противном случае
     */
    @Override
    public boolean canHandle(ProcessType processType) {
        return processType == ProcessType.GET_SERVICE_INFO;
    }

    /**
     * Инициирует процесс получения информации о сервисе: устанавливает начальное состояние сессии
     * и запрашивает у пользователя название сервиса.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя
     */
    @Override
    public void startProcess(long chatId, UserSession session) {
        session.startProcess(ProcessType.GET_SERVICE_INFO);
        sendMessageWithBackButton(chatId, StringText.ENTER_SERVICE_NAME);
        log.info("Начат процесс получения информации о сервисе для пользователя {}", chatId);
    }

    /**
     * Обрабатывает единственный шаг процесса — ввод названия сервиса.
     * При неизвестном состоянии сессия сбрасывается, и отправляется сообщение об ошибке.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput ввод пользователя — название сервиса
     * @param session   сессия пользователя
     * @param messageId идентификатор входящего сообщения (в данном обработчике не используется для удаления)
     */
    @Override
    public void handleStep(long chatId, String userInput, UserSession session, int messageId) {
        ProcessState currentState = session.getProcessState();

        if (currentState == ProcessState.GET_SERVICE_INFO_ENTERING_NAME) {
            handleServiceName(chatId, userInput, session);
        } else {
            log.warn("Неизвестное состояние для GET_SERVICE_INFO: {}", currentState);
            session.resetProcess();
            sendMessageWithMenu(chatId, StringText.PROBLEM_TRY_AGAIN_LATER, false);
        }

        updateSessionActivity(session);
    }

    /**
     * Обрабатывает введённое пользователем название сервиса:
     * нормализует строку, проверяет на пустоту, выполняет запрос к сервису отеля.
     * В случае успеха — отправляет форматированную информацию о сервисе.
     * В случае отсутствия — уведомляет об этом.
     * После обработки сессия сбрасывается.
     *
     * @param chatId      уникальный идентификатор чата в Telegram
     * @param serviceName название сервиса, введённое пользователем
     * @param session     сессия пользователя
     */
    private void handleServiceName(long chatId, String serviceName, UserSession session) {
        String normalizedServiceName = normalizeInutLine(serviceName);

        if (normalizedServiceName.isBlank()) {
            sendMessageWithBackButton(chatId, StringText.PLEASE_ENTER_WRIGHT_DATA);
            return;
        }

        try {
            ResponseEntity<ServiceHotelDTO> response = hotelClientFeign.getByTitle(normalizedServiceName);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ServiceHotelDTO service = response.getBody();
                String message = formatRoomInfo(service);
                sendMessageWithMenu(chatId, message, isUserAuthenticated(chatId));
                log.info("Информация о комнате {} отправлена пользователю chatId, {}", serviceName, chatId);
            } else {
                sendMessageWithMenu(chatId, StringText.SERVICE + normalizedServiceName + StringText.NOT_FOUND,
                        isUserAuthenticated(chatId));
                log.warn("Комната {} не найдена для пользователя chatId, {}", normalizedServiceName, chatId);
            }
        } catch (Exception e) {
            sendMessageWithMenu(chatId, StringText.PROBLEM_TRY_AGAIN_LATER, isUserAuthenticated(chatId));
            log.error("Ошибка при получении информации о комнате {} для пользователя chatId, {}", normalizedServiceName, chatId, e);
        } finally {
            session.resetProcessTypeAndState();
        }

    }

    private String normalizeInutLine(String input) {
        return input.replaceAll("[^\\p{L}]", "").toLowerCase();
    }

    /**
     * Форматирует информацию о сервисе в читаемый вид для отправки пользователю.
     *
     * @param service объект с данными о сервисе
     * @return отформатированная строка с названием, описанием и ценой сервиса
     */
    private String formatRoomInfo(ServiceHotelDTO service) {
        return StringText.SERVICE + StringText.DOUBLE_POINT + service.getTitle() + StringText.NEXT_LINE
                + service.getDescription() + StringText.NEXT_LINE
                + StringText.PRICE + StringText.DOUBLE_POINT + service.getPrice() + StringText.MONEY;
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
