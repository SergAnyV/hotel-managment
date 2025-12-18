package com.asv.services;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
/**
 * Сервис для отправки сообщений в Telegram.
 */
public interface BotSenderMessageService {
    void sendMessage(long chatId, String text);
    void sendMessage(long chatId, String text, ReplyKeyboardMarkup keyboard);
    void deleteMessage(long chatId, int messageId);
}
