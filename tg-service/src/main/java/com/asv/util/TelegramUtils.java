package com.asv.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
/**
 * Утилитарный класс для создания Telegram-сообщений.
 */
public final class TelegramUtils {

    private TelegramUtils() {
        throw new UnsupportedOperationException("Это утилитарный класс , не надо создавать экземпляр");
    }

    public static SendMessage textMessage(long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText(text);
        return msg;
    }

    public static DeleteMessage deleteMessage(long chatId, int messageId) {
        DeleteMessage del = new DeleteMessage();
        del.setChatId(String.valueOf(chatId));
        del.setMessageId(messageId);
        return del;
    }

    public static SendMessage textMessageWithKeyboard(long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage msg = textMessage(chatId, text);
        msg.setReplyMarkup(keyboard);
        return msg;
    }
}
