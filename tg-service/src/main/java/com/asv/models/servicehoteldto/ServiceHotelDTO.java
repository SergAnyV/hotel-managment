package com.asv.models.servicehoteldto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO), представляющий полную информацию об услуге отеля.
 * <p>
 * Используется для передачи данных об отельной услуге (например, уборка, завтрак, трансфер)
 * между клиентом и сервером через REST API — при создании, обновлении или получении сведений об услуге.
 * </p>
 * <p>
 * Содержит название, описание и стоимость услуги за сутки. Поддерживает валидацию входных данных
 * и аннотирован для генерации OpenAPI/Swagger-документации.
 * </p>
 */
@Data
@Builder
@Schema(name = "ServiceHotelDTO", description = "Модель сервиса отеля с ценой")
public class ServiceHotelDTO {

    /**
     * Название услуги отеля.
     * <p>
     * Обязательное поле. Должно содержать от 3 до 20 символов.
     * Примеры: "Уборка", "Завтрак", "Wi-Fi Premium".
     * </p>
     */
    @Schema(description = "Название сервиса", example = "Уборка")
    @NotBlank(message = "Название сервиса не должно быть пустым")
    @Size(min = 3, max = 20, message = "количество символов 3-20 для названия сервиса")
    private String title;

    /**
     * Описание услуги отеля.
     * <p>
     * Обязательное поле. Должно содержать от 3 до 100 символов.
     * Пример: "Ежедневная уборка номера с заменой постельного белья".
     * </p>
     */
    @Schema(description = "Описание сервиса", example = "уборка номера")
    @NotBlank(message = "Описание сервиса не должно быть пустым")
    @Size(min = 3, max = 100, message = "количество символов 3-100 для описания сервиса")
    private String description;

    /**
     * Стоимость услуги за одни сутки.
     * <p>
     * Обязательное поле. Должно быть положительным числом.
     * Точность: до двух знаков после запятой (например, {@code 150.00}).
     * </p>
     */
    @Schema(description = "Цена за сервис в сутки,три знака перед. и два после", example = "150.00")
    @Positive(message = "цена должна быть положительна для сервиса")
    @NotNull(message = "для сервиса цена не может быть null")
    private BigDecimal price;
}
