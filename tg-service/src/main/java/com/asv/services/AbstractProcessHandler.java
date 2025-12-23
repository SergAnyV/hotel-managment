package com.asv.services;

import com.asv.sessions.UserSession;
import com.asv.ui.MenuButtonService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * Абстрактный базовый класс для обработчиков пошаговых процессов взаимодействия с пользователем.
 * Предоставляет общую функциональность для отправки сообщений с различными типами клавиатур,
 * управления сессией и удаления сообщений.
 * Конкретные реализации должны наследоваться от этого класса и реализовывать логику обработки
 * конкретного типа процесса (например, регистрация, бронирование и т.д.).
 */
public abstract class AbstractProcessHandler {
    private final BotSenderMessageService sender;
    private final MenuButtonService menuButtonService;

    protected AbstractProcessHandler(BotSenderMessageService sender,
                                     MenuButtonService menuButtonService) {
        this.sender = sender;
        this.menuButtonService = menuButtonService;
    }

    /**
     * Отправить сообщение с кнопкой "Назад"
     */
    protected void sendMessageWithBackButton(long chatId, String message) {
        sender.sendMessage(chatId, message, menuButtonService.backMenuButton());
    }

    /**
     * Отправить сообщение с нужным меню (авторизованное или стартовое)
     */
    protected void sendMessageWithMenu(long chatId, String message, boolean isAuthenticated) {
        ReplyKeyboardMarkup menu = isAuthenticated
                ? menuButtonService.authMenuButtons()
                : menuButtonService.startMenuButtons();
        sender.sendMessage(chatId, message, menu);
    }
    /**
     * Отправить сообщение с кастомным меню
     */
    protected void sendMessageWithMenu(long chatId, String message, ReplyKeyboardMarkup menu) {
        sender.sendMessage(chatId, message, menu);
    }

    /**
     * Проверить и обновить активность сессии
     */
    protected void updateSessionActivity(UserSession session) {
        session.setProcessState(session.getProcessState());
    }

    /**
     * Удалить полученное сообщение
     */
    protected void deleteMessage(long chatId, int messageId) {
        sender.deleteMessage(chatId, messageId);
    }

}
