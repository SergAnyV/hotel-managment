package com.asv.services;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotService {
    /**
     * Основной метод обработки входящих сообщений от Telegram.
     */
    void handleUpdate(Update update);
}
