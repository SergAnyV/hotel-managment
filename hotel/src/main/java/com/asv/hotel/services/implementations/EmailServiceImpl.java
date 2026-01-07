package com.asv.hotel.services.implementations;

import com.asv.hotel.exceptions.HotelNotificationException;
import com.asv.hotel.services.EmailService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
/**
 * Реализация сервиса отправки email-уведомлений.
 * <p>
 * Использует JavaMail API для асинхронной отправки простых текстовых сообщений.
 * Настройки SMTP подключения инжектятся через Spring-бин {@code Session}.
 * </p>
 */
@Service
public class EmailServiceImpl implements EmailService {

    /**
     * Настроенная сессия JavaMail для подключения к SMTP-серверу.
     */
    private final Session session;
    /**
     * Адрес отправителя (email по умолчанию, указанный в конфигурации).
     */
    private final String from;

    public EmailServiceImpl(@Qualifier("getMailSession") Session session,
                            @Value("${mail.from}") String from) {
        this.session = session;
        this.from = from;
    }


    /**
     * Асинхронно отправляет текстовое email-сообщение.
     * <p>
     * Выполняется в отдельном потоке благодаря аннотации {@link Async}.
     * В случае ошибки отправки выбрасывает {@link HotelNotificationException}.
     * </p>
     *
     * @param to      email-адрес получателя
     * @param subject тема письма
     * @param message текст сообщения
     * @throws HotelNotificationException если произошла ошибка при отправке (например, недоступен SMTP-сервер)
     */
    @Async
    @Override
    public void sendNotificationEmail(String to, String subject, String message) {
        try {
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(from));
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        mimeMessage.setSubject(subject);
        mimeMessage.setText(message);
        Transport.send(mimeMessage);
        } catch (MessagingException e) {
           throw new HotelNotificationException(
                   String.format("Problem with sending email to: '%s' ,subject: '%s' ,message: '%s'", to, subject, message));
        }
    }
}
