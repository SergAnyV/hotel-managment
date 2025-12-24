package com.asv.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * Конфигурация Telegram-бота.
 * Читает токен и имя из properties и создаёт бин бота.
 */
@Component
@ConfigurationProperties(prefix = "telegram.bot")
@Getter
@Setter
public class BotConfig {
    private String token;
    private String username;
}
