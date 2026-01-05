package com.asv.hotel.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Сущность уведомления в системе отеля.
 * <p>
 * Представляет сообщение, отправленное пользователю (сотруднику или клиенту).
 * Каждое уведомление может быть привязано к конкретному бронированию (опционально)
 * или существовать независимо (например, системное сообщение).
 * </p>
 * <p>
 * Поддерживает автоматическую установку временной метки создания.
 * </p>
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationHotel {

    /**
     * Уникальный идентификатор уведомления.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текст сообщения уведомления.
     * Обязательное поле.
     */
    @Column(name = "message", nullable = false)
    private String message;

    /**
     * Дата и время создания уведомления.
     * Устанавливается автоматически при сохранении и не изменяется впоследствии.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Пользователь, которому адресовано уведомление.
     * Обязательная связь.
     */
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Бронирование, с которым связано уведомление (например, напоминание о заезде).
     * Связь необязательная — уведомление может быть общим.
     */
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = true)
    private Booking booking;
}
