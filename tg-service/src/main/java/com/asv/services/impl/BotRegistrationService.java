package com.asv.services.impl;

import com.asv.bots.HotelClientTB;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotRegistrationService {

    private final HotelClientTB hotelClientTB;

    @Retryable(
            value = TelegramApiException.class,
            maxAttempts = 10,
            backoff = @Backoff(delay = 3_000)
    )
    public void registerBot() throws TelegramApiException {

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(hotelClientTB);
        log.info("Telegram-бот успешно запущен");

    }

    @Recover
    public void recover(RuntimeException e) {
        log.warn("Превышено количество попыток: {}", e.getCause().getMessage());
    }
}
