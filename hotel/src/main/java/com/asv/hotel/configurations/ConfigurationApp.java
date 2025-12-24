package com.asv.hotel.configurations;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Properties;

@Configuration
public class ConfigurationApp {

    @Bean(value = "getMailSession")
    public Session getMailSession(MailProperties mailProperties) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailProperties.getHost());
        properties.put("mail.smtp.port", mailProperties.getPort());
        properties.put("mail.smtp.auth", mailProperties.isAuth());
        properties.put("mail.smtp.starttls.enable", mailProperties.isStarttlsEnable());
        properties.put("mail.smtp.ssl.trust", mailProperties.getHost());
return Session.getInstance(properties, new Authenticator() {
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mailProperties.getUsername(), mailProperties.getPassword());
    }
});
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(5);
        return messageSource;
    }
}
