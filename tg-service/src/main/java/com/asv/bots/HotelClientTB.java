package com.asv.bots;

import com.asv.configuration.BotConfig;
import com.asv.services.BotService;
import com.asv.services.impl.BotServiceImpl;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HotelClientTB extends TelegramLongPollingBot {
    private final String botUserName;
    private final BotService botService;

    public HotelClientTB(BotConfig botConfig, BotServiceImpl botService) {
        super(botConfig.getToken());
        this.botUserName = botConfig.getUsername();
        this.botService = botService;
    }

    @Override
    public void onUpdateReceived(Update update) {

        botService.handleUpdate(update);
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

}
