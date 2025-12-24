package com.asv.sessions;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище пользовательских сессий в памяти.
 * Использует ConcurrentHashMap для потокобезопасности.
 * Регулярно очищает неактивные сессии (старше 30 минут).
 */
@Component
public class UserSessionStorage {


    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();
    private static final long NOT_ACTIVE_TIME_LIMIT=10L * 60 * 1000;


    /**
     * Возвращает существующую сессию или создаёт новую.
     */
    public UserSession getOrCreate(long chatId) {
        return sessions.computeIfAbsent(chatId, k -> new UserSession());
    }

    /**
     * Удаляет сессию пользователя.
     */
    public void remove(long chatId) {
        sessions.remove(chatId);
    }

    /**
     * Фоновая задача: удаляет сессии(каждые 5 минут=300000млс), неактивные более 10 минут.
     */
    @Scheduled(fixedRate = 300000)
    public void cleanupInactive() {
       final long now = System.currentTimeMillis();
        sessions.entrySet().removeIf(entry ->
                (now - entry.getValue().getLastActivity()) > NOT_ACTIVE_TIME_LIMIT
        );
    }
}
