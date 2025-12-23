package com.asv.models.promocodedto;


import com.asv.models.enums.TypeOfPromoCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO), представляющий промокод для предоставления скидок при бронировании.
 * <p>
 * Используется для передачи данных о промокоде между клиентом и сервером,
 * например, при создании, обновлении или валидации промокода в системе отеля.
 * </p>
 * <p>
 * Поддерживает два типа скидок:
 * <ul>
 *   <li>{@code FIXED} — фиксированная сумма скидки (например, 1000 рублей);</li>
 *   <li>{@code PERCENT} — процентная скидка (например, 10%).</li>
 * </ul>
 * </p>
 * <p>
 * Содержит валидацию входных данных с помощью Jakarta Bean Validation и аннотирован
 * для генерации OpenAPI/Swagger-документации.
 * </p>
 *
 * @see TypeOfPromoCode
 */
@Data
@Builder
@Schema(name = "Модель данных промокода", description = "модель передачи данных через JSON")
public class PromoCodeDTO {

    /**
     * Уникальный строковый идентификатор промокода.
     * <p>
     * Обязательное поле. Должен содержать от 1 до 20 символов.
     * Допускаются только буквы (кириллица или латиница) и цифры.
     * Пример: "WELCOME10", "Халява2025".
     * </p>
     */
    @Schema(description = "промокод", example = "Халява")
    @NotBlank(message = "промокод не должен быть пустым")
    @Size(max = 20, message = "Длина промокода не должна превышать 20 символов")
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$", message = "Промокод может содержать только буквы, цифры ")
    private String code;

    /**
     * Тип скидки, предоставляемой промокодом.
     * <p>
     * Обязательное поле. Возможные значения:
     * <ul>
     *   <li>{@link TypeOfPromoCode#FIXED} — фиксированная сумма;</li>
     *   <li>{@link TypeOfPromoCode#PERCENT} — процент от общей стоимости.</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Тип промокода", example = "FIXED", allowableValues = {"FIXED", "PERCENT"})
    @NotNull(message = "Тип промокода обязателен")
    private TypeOfPromoCode typeOfPromoCode;

    /**
     * Значение скидки.
     * <p>
     * Обязательное поле. Интерпретация зависит от {@link #typeOfPromoCode}:
     * <ul>
     *   <li>Для {@code FIXED} — сумма в валюте (например, {@code 1000.00});</li>
     *   <li>Для {@code PERCENT} — процент (например, {@code 10.00} означает 10%).</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Значение скидки", example = "10.00")
    @NotNull(message = "Значение скидки обязательно")
    private BigDecimal discountValue;

    /**
     * Дата начала действия промокода (включительно).
     * <p>
     * Обязательное поле. Должна быть сегодняшней или будущей датой.
     * </p>
     */
    @Schema(description = "Дата начала действия", example = "2023-06-01")
    @NotNull(message = "Дата начала действия обязательна")
    @FutureOrPresent
    private LocalDate validFromDate;

    /**
     * Дата окончания действия промокода (включительно).
     * <p>
     * Обязательное поле. Должна быть сегодняшней или будущей датой.
     * Должна быть позже или равна {@link #validFromDate}.
     * (Примечание: логическая проверка последовательности дат должна выполняться на уровне сервиса.)
     * </p>
     */
    @Schema(description = "Дата окончания действия", example = "2023-07-01")
    @NotNull(message = "Дата окончания действия обязательна")
    @FutureOrPresent
    private LocalDate validUntilDate;

    /**
     * Флаг активности промокода.
     * <p>
     * Определяет, может ли промокод быть использован клиентами.
     * Обязательное поле. Допустимые значения: {@code true} или {@code false}.
     * По умолчанию предполагается {@code true}.
     * </p>
     */
    @Schema(description = "Активен ли промокод true/false",
            example = "true",
            defaultValue = "true",
            allowableValues = {"true", "false"} )
    @NotNull(message = "активен или нет обязательно")
    private Boolean isActive;

}
