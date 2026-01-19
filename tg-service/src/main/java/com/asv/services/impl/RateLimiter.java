package com.asv.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RateLimiter {

    private final ConcurrentHashMap<Long, AtomicInteger> counters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Long> intervals = new ConcurrentHashMap<>();

    private final long windowSeconds;
    private final int maxRequests;

    public RateLimiter() {
        this.windowSeconds = 5;
        this.maxRequests = 3;
    }

    public boolean allow(long chatId) {
        long now = Instant.now().getEpochSecond();
        long windowStart = now - windowSeconds;

        intervals.computeIfPresent(chatId, (id, start) -> {
            if (start < windowStart) {
                counters.remove(id);
                return null;
            }
            return start;
        });

        intervals.computeIfAbsent(chatId, id -> now);

        AtomicInteger counter = counters.computeIfAbsent(chatId, id -> new AtomicInteger(0));
        int current = counter.incrementAndGet();

        if (current > maxRequests) {
            log.warn("Rate limit exceeded for chatId={}. Current: {}, Max: {}", chatId, current, maxRequests);
            return false;
        }

        return true;
    }

    public void reset(long chatId) {
        counters.remove(chatId);
        intervals.remove(chatId);
    }

    /**
     * Очищает все устаревшие записи каждые 5 минут.
     */
    @Scheduled(fixedRate = 300_000)
    public void cleanupExpiredEntries() {
        long now = Instant.now().getEpochSecond();
        long windowStart = now - windowSeconds;


        var expiredChatIds = intervals.entrySet().stream()
                .filter(entry -> entry.getValue() < windowStart)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!expiredChatIds.isEmpty()) {
            log.debug("Очистка {} устаревших записей из rate limiter", expiredChatIds.size());
            for (Long chatId : expiredChatIds) {
                counters.remove(chatId);
                intervals.remove(chatId);
            }
        }
    }
}