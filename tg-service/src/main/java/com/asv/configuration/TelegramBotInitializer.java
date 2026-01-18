package com.asv.configuration;

import com.asv.bots.HotelClientTB;
import com.asv.services.impl.BotRegistrationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class TelegramBotInitializer {
//
//    private final HotelClientTB hotelClientTB;
//
//    @PostConstruct
//    public void registerBot() throws TelegramApiException {
//        tryRegisterBot();
//    }
//
//    @Retryable(
//            value = TelegramApiException.class,
//            maxAttempts = 10,
//            backoff = @Backoff(delay = 3_000)
//    )
//    public void tryRegisterBot() throws TelegramApiException {
//        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//        botsApi.registerBot(hotelClientTB);
//        log.info("Telegram-бот успешно запущен");
//    }
//
//    @Recover
//    public void recover(TelegramApiException e) {
//        log.warn("Превышено количество попыток запуска бота. Ошибка: {}", e.getMessage());
//    }
//}

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotInitializer {

    private final HotelClientTB hotelClientTB;
    private final BotRegistrationService botRegistrationService;

    @PostConstruct
    public void registerBot() throws TelegramApiException{
        botRegistrationService.registerBot();
    }
}