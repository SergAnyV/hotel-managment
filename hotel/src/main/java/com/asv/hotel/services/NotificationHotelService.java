package com.asv.hotel.services;

import com.asv.hotel.dto.notificationdto.NotificationHotelDto;
import com.asv.hotel.entities.Booking;
import com.asv.hotel.entities.NotificationHotel;
import com.asv.hotel.entities.User;

public interface NotificationHotelService {
    NotificationHotelDto createNotificationBooking(String message, Booking booking, String subject);
    NotificationHotel createNotificationNewUserVerifying(String message, User user, String subject);
}
