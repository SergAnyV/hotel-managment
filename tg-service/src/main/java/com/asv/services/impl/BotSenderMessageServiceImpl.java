package com.asv.services.impl;

import com.asv.bots.HotelClientTB;
import com.asv.services.BotSenderMessageService;
import com.asv.util.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Реализация сервиса отправки и удаления сообщений в Telegram.
 * Обеспечивает инкапсуляцию работы с Telegram Bot API и обработку возможных исключений.
 * Использует утилитный класс {@link TelegramUtils} для формирования сообщений.
 */
@Service
@Slf4j
public class BotSenderMessageServiceImpl implements BotSenderMessageService {
    @Lazy
    private final HotelClientTB bot;

    public BotSenderMessageServiceImpl(@Lazy HotelClientTB bot) {
        this.bot = bot;
    }

    /**
     * Отправляет текстовое сообщение в указанный чат без клавиатуры.
     *
     * @param chatId уникальный идентификатор чата в Telegram
     * @param text   текст сообщения
     */
    public void sendMessage(long chatId, String text) {
        try {
            bot.execute(TelegramUtils.textMessage(chatId, text));
        } catch (TelegramApiException e) {
            log.error("Не отправилось в {}: {}", chatId, e.getMessage());
        }
    }

    /**
     * Отправляет текстовое сообщение в указанный чат с прикреплённой клавиатурой.
     *
     * @param chatId   уникальный идентификатор чата в Telegram
     * @param text     текст сообщения
     * @param keyboard клавиатура, прикрепляемая к сообщению
     */
    public void sendMessage(long chatId, String text, ReplyKeyboardMarkup keyboard) {
        try {
            SendMessage message = TelegramUtils.textMessage(chatId, text);
            message.setReplyMarkup(keyboard);
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки с клавиатурой в {}: {}", chatId, e.getMessage());
        }
    }

    /**
     * Удаляет указанное сообщение из чата.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param messageId идентификатор сообщения, подлежащего удалению
     */
    public void deleteMessage(long chatId, int messageId) {
        try {
            bot.execute(TelegramUtils.deleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            log.warn("Не удалось удалить сообщение {} в чате {}: {}", messageId, chatId, e.getMessage());
        }
    }

}
