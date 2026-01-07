package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.mapper.NotificationMapper;
import com.asv.hotel.dto.notificationdto.NotificationHotelDto;
import com.asv.hotel.entities.Booking;
import com.asv.hotel.entities.NotificationHotel;
import com.asv.hotel.entities.User;
import com.asv.hotel.repositories.NotificationRepository;
import com.asv.hotel.services.EmailService;
import com.asv.hotel.services.NotificationHotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Реализация сервиса уведомлений для отеля.
 * <p>
 * Обеспечивает создание уведомлений и отправку email-сообщений:
 * <ul>
 *   <li>для событий, связанных с бронированием (например: подтверждение брони);</li>
 *   <li>для системных событий, не привязанных к бронированию (например: подтверждение регистрации).</li>
 * </ul>
 * </p>
 * <p>
 * Каждое уведомление сохраняется в БД и дублируется email-сообщением.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class NotificationHotelServiceImpl implements NotificationHotelService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    /**
     * Создаёт уведомление, связанное с бронированием, и отправляет email пользователю.
     * <p>
     * Уведомление привязывается к пользователю и бронированию из контекста.
     * Email отправляется асинхронно (в зависимости от реализации {@link EmailService}).
     * </p>
     *
     * @param message  текст уведомления
     * @param booking  бронирование, с которым связано уведомление
     * @param subject  тема email-письма
     * @return DTO созданного уведомления
     */
    @Transactional
    @Override
    public NotificationHotelDto createNotificationBooking(String message, Booking booking, String subject) {
        NotificationHotel notificationHotel = NotificationHotel.builder()
                .message(message)
                .user(booking.getUser())
                .booking(booking)
                .build();

        emailService.sendNotificationEmail(
                booking.getUser().getEmail(),
                subject,
                message
        );
        notificationHotel = notificationRepository.save(notificationHotel);
        return NotificationMapper.INSTANCE.notificationToNotificationDTO(notificationHotel);
    }

    /**
     * Создаёт системное уведомление (не привязанное к бронированию) и отправляет email пользователю.
     * <p>
     * Используется, например, для подтверждения регистрации или сброса пароля.
     * Поле {@code booking} в уведомлении остаётся {@code null}.
     * </p>
     *
     * @param message  текст уведомления
     * @param user     получатель уведомления
     * @param subject  тема email-письма
     * @return сохранённая сущность уведомления
     */
    @Transactional
    public NotificationHotel createNotificationNewUserVerifying(String message, User user, String subject) {
        NotificationHotel notificationHotel = NotificationHotel.builder()
                .message(message)
                .user(user)
                .booking(null)
                .build();
        emailService.sendNotificationEmail(user.getEmail(), subject, message);
        return notificationRepository.save(notificationHotel);
    }

}
