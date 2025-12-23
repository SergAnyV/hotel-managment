package com.asv.hotel.dto.servicehoteldto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
/**
 * Упрощённый Data Transfer Object (DTO) для представления услуги отеля без указания цены.
 * <p>
 * Используется в сценариях, где требуется передать только идентифицирующую информацию об услуге —
 * её название и описание — без финансовых деталей.
 * </p>
 * <p>
 * Типичные случаи применения:
 * <ul>
 *   <li>Выбор услуг при создании бронирования;</li>
 *   <li>Отображение списка доступных услуг в клиентском интерфейсе;</li>
 * </ul>
 * </p>
 */
@Data
@Builder
@Schema(name = "ServiceHotelSimpleDTO", description = "Упрощенная модель сервиса отеля без цены")
public class ServiceHotelSimpleDTO {

    /**
     * Название услуги отеля.
     * <p>
     * Обязательное поле. Должно содержать от 3 до 20 символов.
     * Примеры: "Уборка", "Завтрак", "Трансфер".
     * </p>
     */
    @Schema(description = "Название сервиса", example = "Уборка")
    @NotBlank(message = "Название сервиса не должно быть пустым")
    @Size(min = 2, max = 20, message = "количество символов 3-20 для названия сервиса")
    private String title;

    /**
     * Описание услуги отеля.
     * <p>
     * Обязательное поле. Должно содержать от 3 до 100 символов.
     * Пример: "Ежедневная уборка номера с заменой белья".
     * </p>
     */
    @Schema(description = "Описание сервиса", example = "уборка номера")
    @NotBlank(message = "Описание сервиса не должно быть пустым")
    @Size(min = 3, max = 100, message = "количество символов 3-100 для описания сервиса")
    private String description;
}
