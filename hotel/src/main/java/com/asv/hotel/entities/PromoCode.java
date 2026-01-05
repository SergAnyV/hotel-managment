package com.asv.hotel.entities;

import com.asv.hotel.entities.enums.TypeOfPromoCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сущность промокода для предоставления скидок при бронировании.
 * <p>
 * Описывает уникальный код, тип и размер скидки, а также период действия и статус активности.
 * </p>
 * <p>
 * Текстовое описание промокода автоматически устанавливается на основе его типа
 * при сохранении или обновлении через JPA-колбэк.
 * </p>
 */
@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {

    /**
     * Уникальный идентификатор промокода.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текстовый код промокода (например: "WELCOME10").
     * Обязательное, уникальное поле, до 20 символов.
     */
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;


    /**
     * Тип скидки: фиксированная сумма или процент.
     * Используется строковое представление перечисления {@link TypeOfPromoCode}.
     */
    @Column(name = "discount_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TypeOfPromoCode typeOfPromoCode;


    /**
     * Описание типа скидки (например: "Фиксированная скидка в рублях").
     * Автоматически заполняется из {@link TypeOfPromoCode#getDescription()} при сохранении.
     */
    @Column(name = "description", length = 100)
    private String description;


    /**
     * Значение скидки: сумма в рублях или процент (в зависимости от типа).
     * Обязательное поле с точностью до 2 знаков после запятой.
     */
    @Column(name = "discount_value", nullable = false, precision = 8, scale = 2)
    private BigDecimal discountValue;


    /**
     * Дата начала действия промокода (включительно).
     * Обязательное поле.
     */
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFromDate;


    /**
     * Дата окончания действия промокода (включительно).
     * Обязательное поле.
     */
    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntilDate;

    /**
     * Флаг активности: указывает, может ли промокод использоваться.
     * {@code true} — активен, {@code false} — деактивирован (даже если дата валидна).
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * Колбэк JPA: автоматически устанавливает поле {@code description}
     * на основе описания из перечисления {@link TypeOfPromoCode}
     * перед сохранением или обновлением сущности.
     */
    @PrePersist
    @PreUpdate
    private void preUpdate() {
        if (typeOfPromoCode != null) {
            this.description = typeOfPromoCode.getDescription();
        }
    }

}
