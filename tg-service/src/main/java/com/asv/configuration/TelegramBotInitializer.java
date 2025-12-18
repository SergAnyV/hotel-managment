package com.asv.configuration;

import com.asv.bots.HotelClientTB;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotInitializer {
    private final HotelClientTB hotelClientTB;
    private int repeaterCounter = 0;

    @PostConstruct
    public void registerBot() {
        if (repeaterCounter >= 10) {
            log.warn("Превышено количество попыток запуска бота");
            return;
        }
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(hotelClientTB);
            log.info("Telegram-бот успешно запущен");
        } catch (TelegramApiException e) {
            log.error("Ошибка при запуске Telegram-бота: {}", e.getMessage());
            repeatStart();
        }
    }

    private void repeatStart() {
        repeaterCounter++;
        try {
            Thread.sleep(10_000);
            registerBot();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Ошибка при перезапуске Telegram-бота: {}", ex.getMessage());
        }
    }
}
