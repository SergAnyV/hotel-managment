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

@Service
public class EmailServiceImpl implements EmailService {
    private final Session session;
    private final String from;

    public EmailServiceImpl(@Qualifier("getMailSession") Session session,
                            @Value("${mail.from}") String from) {
        this.session = session;
        this.from = from;
    }
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
