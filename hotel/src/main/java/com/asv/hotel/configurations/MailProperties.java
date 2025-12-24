package com.asv.hotel.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * Класс конфигурационных свойств для настройки SMTP-клиента электронной почты.
 * <p>
 * Свойства автоматически загружаются из внешних источников конфигурации
 * с префиксом {@code mail.smtp}.
 * </p>
 */
@ConfigurationProperties(prefix = "mail.smtp")
@Getter
@Setter
@Component
public class MailProperties {
    /**
     * Хост SMTP-сервера (например, {@code smtp.gmail.com}).
     */
    private String host;
    /**
     * Порт SMTP-сервера (обычно 587 для STARTTLS или 465 для SSL).
     */
    private int port;
    /**
     * Флаг, указывающий, требуется ли аутентификация при подключении к SMTP-серверу.
     */
    private boolean auth;
    /**
     * Флаг, включающий поддержку STARTTLS для шифрования соединения.
     */
    private boolean starttlsEnable;
    /**
     * Имя пользователя (логин) для аутентификации на SMTP-сервере.
     */
    private String username;
    /**
     * Пароль для аутентификации на SMTP-сервере.
     */
    private String password;
}
