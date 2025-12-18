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

@RequiredArgsConstructor
@Service
public class NotificationHotelServiceImpl implements NotificationHotelService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

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
