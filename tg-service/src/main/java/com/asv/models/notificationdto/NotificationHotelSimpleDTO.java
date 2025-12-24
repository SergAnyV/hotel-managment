package com.asv.models.notificationdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Простой DTO для передачи данных уведомления о бронировании отеля.
 * <p>
 * Используется в качестве входного или выходного объекта в REST-контрактах.
 * Содержит минимально необходимый набор полей для формирования уведомления:
 * текст сообщения, никнейм пользователя и идентификатор бронирования.
 * </p>
 * <p>
 * Все поля обязательны и проходят валидацию с помощью Jakarta Bean Validation.
 * Документация для OpenAPI (Swagger) генерируется автоматически благодаря аннотациям .
 */
public class NotificationHotelSimpleDTO {
    /**
     * Текст уведомления, отображаемый пользователю.
     * <p>
     * Должен содержать от 3 до 100 символов и не быть пустым.
     */
    @Schema(description = "сообщение предаваемое в уведомлении", example = "номер забронирован")
    @NotBlank(message = "сообщение не должно быть пустым быть пустым")
    @Size(min = 3, max = 100, message = "количество символов 3-100")
    private String message;
    /**
     * Отображаемое имя (никнейм) пользователя, которому адресовано уведомление.
     * <p>
     * Должен содержать от 3 до 20 символов и не быть пустым.
     */
    @Schema(description = "никнайм пользователя", example = "BigBro")
    @NotBlank(message = "никнайм пользователя, не должен быть пустым")
    @Size(min = 3, max = 20, message = "количество символов 3-20")
    private String nickName;
    /**
     * Уникальный идентификатор бронирования.
     * <p>
     * Обязательное числовое значение, не может быть null.
     */
    @Schema(description = "уникальный номер бронирования", example = "1")
    @NotNull(message = "уникальный номер бронирования обязателен")
    private Long bookingId;
}
