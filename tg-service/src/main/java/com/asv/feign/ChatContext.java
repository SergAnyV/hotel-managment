package com.asv.feign;
/**
 * Контекст для передачи chatId из обработчика Telegram в Feign Interceptor.
 * Использует ThreadLocal для изоляции запросов в многопоточной среде.
 */
public class ChatContext {

    private static final ThreadLocal<Long> CURRENT_CHAT_ID = new ThreadLocal<>();

    private ChatContext() {
        throw new UnsupportedOperationException("Это утилитарный класс , не надо создавать экземпляр");
    }

    /**
     * Установить chatId в локальный поток.
     */
    public static void setChatId(Long chatId) {
        CURRENT_CHAT_ID.set(chatId);
    }

    /**
     * Получить chatId из локального потока.
     */
    public static Long getChatId() {
        return CURRENT_CHAT_ID.get();
    }

    /**
     * Удалить chatId из локального потока.  ОБЯЗАТЕЛЬНО вызывать в finally!
     */
    public static void clear() {
        CURRENT_CHAT_ID.remove();
    }
}
