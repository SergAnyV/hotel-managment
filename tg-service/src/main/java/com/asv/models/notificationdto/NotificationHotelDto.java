package com.asv.models.notificationdto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object), представляющий уведомление, связанное с бронированием отеля.
 * <p>
 * Используется для передачи данных об уведомлении между компонентами приложения,
 * например, при отправке email, push-уведомлений или отображении в интерфейсе.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationHotelDto {
    /**
     * Текст уведомления (например, "Бронирование подтверждено" или "Напоминание о заселении").
     */
    private String message;
    /**
     * Дата и время создания уведомления.
     */
    private LocalDateTime createdAt;
    /**
     * Отображаемое имя (никнейм) пользователя, связанного с уведомлением.
     */
    private String nickName;
    /**
     * Идентификатор бронирования, к которому относится данное уведомление.
     */
    private Long bookingId;

}
