package com.asv.hotel.services;

import com.asv.hotel.dto.notificationdto.NotificationHotelDto;
import com.asv.hotel.entities.Booking;
import com.asv.hotel.entities.NotificationHotel;
import com.asv.hotel.entities.User;
/**
 * Сервис для создания уведомлений и отправки email-сообщений пользователям.
 * <p>
 * Обеспечивает генерацию системных уведомлений как для событий, связанных с бронированием,
 * так и для общих операций (например, подтверждение регистрации).
 * </p>
 */
public interface NotificationHotelService {
    /**
     * Создаёт уведомление, связанное с бронированием, и отправляет email пользователю.
     *
     * @param message  текст уведомления
     * @param booking  бронирование, с которым связано уведомление
     * @param subject  тема email-письма
     * @return DTO созданного уведомления
     */
    NotificationHotelDto createNotificationBooking(String message, Booking booking, String subject);

    /**
     * Создаёт системное уведомление (не привязанное к бронированию) и отправляет email пользователю.
     * <p>
     * Используется, например, для подтверждения регистрации или сброса пароля.
     * </p>
     *
     * @param message  текст уведомления
     * @param user     получатель уведомления
     * @param subject  тема email-письма
     * @return сохранённая сущность уведомления
     */
    NotificationHotel createNotificationNewUserVerifying(String message, User user, String subject);
}
