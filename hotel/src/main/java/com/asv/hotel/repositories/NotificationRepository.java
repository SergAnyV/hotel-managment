package com.asv.hotel.repositories;

import com.asv.hotel.entities.NotificationHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationHotel,Long> {
    List<NotificationHotel> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<NotificationHotel> findByBookingIdOrderByCreatedAtDesc(Long bookingId);
}
