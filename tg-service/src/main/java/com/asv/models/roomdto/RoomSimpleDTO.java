package com.asv.models.roomdto;


import com.asv.models.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
/**
 * Простой DTO для представления данных о номере отеля.
 * <p>
 * Используется при создании, обновлении или передаче информации о комнате через REST API.
 * Содержит основные атрибуты: номер, тип, описание, вместимость и стоимость за ночь.
 * Все поля проходят валидацию с помощью Jakarta Bean Validation.
 * </p>
 */
@Data
@Builder
public class RoomSimpleDTO {
    /**
     * Уникальный номер комнаты (например, "101", "A205").
     * <p>
     * Должен быть непустым и содержать только буквы (кириллица/латиница) и цифры.
     */
    @Schema(description = "номер комнаты", example = "101")
    @NotBlank(message = "номер комнаты не должен быть пустым")
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$", message = "Комната может содержать только буквы, цифры ")
    private String number;
    /**
     * Тип комнаты, определяющий её категорию и уровень комфорта.
     * <p>
     * Допустимые значения: {@code ECONOM}, {@code STANDART}, {@code LUXE}, {@code DELUXE}.
     */
    @Schema(description = "Тип комнаты (стандарт, люкс и т.д.)", allowableValues = {"ECONOM", "STANDART", "LUXE", "DELUXE"})
    @NotBlank(message = "тип комнаты не должен быть пустым")
    private RoomType type;
    /**
     * Описание комнаты, включая особенности и предоставляемые удобства.
     * <p>
     * Должно содержать от 3 до 100 символов и не быть пустым.
     */
    @Schema(description = "Описание комнаты и удобств", example = "Номер с видом на море")
    @NotBlank(message = "Описание комнаты не должен быть пустым")
    @NotBlank(message = " не должен быть пустым")
    @Size(min = 3, max = 100, message = "количество символов 3-100")
    private String description;
    /**
     * Максимальная вместимость комнаты в человеках.
     * <p>
     * Должна быть в диапазоне от 1 до 10.
     */
    @Schema(description = "Вместимость (количество человек)", example = "2", type = "integer")
    @Min(value = 1, message = "Вместимость должна быть не менее 1")
    @Max(value = 10, message = "Вместимость должна быть не менее 10")
    private Integer capacity;
    /**
     * Стоимость проживания за одну ночь в рублях (или другой валюте).
     * <p>
     * Должна быть строго положительной.
     */
    @Schema(description = "Цена за ночь", example = "1500.00")
    @Positive(message = "цена должна быть положительна")
    private BigDecimal pricePerNight;

}