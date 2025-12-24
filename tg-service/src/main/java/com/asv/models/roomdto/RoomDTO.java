package com.asv.models.roomdto;



import com.asv.models.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Data Transfer Object (DTO), представляющий полную информацию о номере отеля.
 * <p>
 * Используется для передачи данных о номере между клиентом и сервером.
 * </p>
 * <p>
 * Содержит все ключевые атрибуты номера: идентификатор, тип, описание, вместимость,
 * стоимость, статус доступности и временные метки создания/обновления.
 * </p>
 * <p>
 * Поля {@code createdAt} и {@code updatedAt} помечены как {@code READ_ONLY},
 * так как управляются системой автоматически и не должны передаваться клиентом.
 * </p>
 * <p>
 * Поддерживает валидацию входных данных с помощью Jakarta Bean Validation
 * и аннотирован для генерации OpenAPI/Swagger-документации.
 * </p>
 *
 * @see RoomType
 */
@Data
@Builder
@Schema(name = "Модель данных номера отеля", description = "модель передачи данных через JSON")
public class RoomDTO {

    /**
     * Уникальный номер комнаты в отеле.
     * <p>
     * Обязательное поле. Должен содержать только буквы (кириллица или латиница) и цифры.
     * Примеры: "101", "A205", "Люкс3".
     * </p>
     */
    @Schema(description = "номер комнаты", example = "101")
    @NotBlank(message = "номер комнаты не должен быть пустым")
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$", message = "Комната может содержать только буквы, цифры ")
    private String number;

    /**
     * Тип номера, определяющий его категорию и уровень комфорта.
     * <p>
     * Обязательное поле. Возможные значения: {@code ECONOM}, {@code STANDART}, {@code LUXE}, {@code DELUXE}.
     * </p>
     *
     * @see RoomType
     */
    @Schema(description = "Тип комнаты (стандарт, люкс и т.д.)", allowableValues = {"ECONOM", "STANDART", "LUXE", "DELUXE"})
    @NotNull
    private RoomType type;

    /**
     * Описание номера и предоставляемых удобств.
     * <p>
     * Обязательное поле. Должно содержать от 3 до 100 символов.
     * Пример: "Номер с видом на море, двуспальная кровать, мини-бар".
     * </p>
     */
    @Schema(description = "Описание комнаты и удобств", example = "Номер с видом на море")
    @NotBlank(message = "Описание комнаты не должен быть пустым")
    @Size(min = 3, max = 100, message = "количество символов 3-100")
    private String description;

    /**
     * Максимальная вместимость номера (количество гостей).
     * <p>
     * Обязательное поле. Допустимый диапазон: от 1 до 10 человек.
     * </p>
     */
    @Schema(description = "Вместимость (количество человек)", example = "2", type = "integer")
    @Min(value = 1, message = "Вместимость должна быть не менее 1")
    @Max(value = 10, message = "Вместимость должна быть не более 10")
    private Integer capacity;

    /**
     * Стоимость проживания за одну ночь в данном номере.
     * <p>
     * Обязательное поле. Должно быть положительным числом.
     * Пример: {@code 1500.00}.
     * </p>
     */
    @Schema(description = "Цена за ночь", example = "1500.00")
    @Positive(message = "цена должна быть положительна")
    private BigDecimal pricePerNight;

    /**
     * Флаг доступности номера для бронирования.
     * <p>
     * Обязательное поле. Если {@code false}, номер не может быть забронирован.
     * </p>
     */
    @Schema(description = "Доступность номера для бронирования", example = "true", allowableValues = {"true", "false"})
    @NotNull(message = "Поле isAvailable обязательно")
    private Boolean isAvailable;
    /**
     * Дата и время создания записи о номере в системе.
     * <p>
     * Поле доступно только для чтения. Устанавливается автоматически при создании.
     * </p>
     */
    @Schema(description = "Дата создания записи", example = "2023-01-01T12:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    @PastOrPresent
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления записи о номере.
     * <p>
     * Поле доступно только для чтения. Обновляется автоматически при изменении данных.
     * Может быть {@code null}, если номер ещё не обновлялся.
     * </p>
     */
    @Schema(description = "Дата последнего обновления", example = "2023-01-02T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;

}
