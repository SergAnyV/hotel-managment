package com.asv.hotel.services;

import java.util.List;

public interface EmailService {
    void sendNotificationEmail(String to, String subject, String message);
}
