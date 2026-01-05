package com.asv.hotel.entities;

import com.asv.hotel.entities.enums.RoomType;
import jakarta.persistence.*;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность номера в отеле.
 * <p>
 * Описывает характеристики комнаты: номер, тип, вместимость, стоимость за ночь
 * и статус доступности.
 * </p>
 * <p>
 * Каждый номер:
 * <ul>
 *   <li>имеет уникальный текстовый идентификатор;</li>
 *   <li>принадлежит к определённому типу (например: СТАНДАРТ, ЛЮКС);</li>
 *   <li>автоматически получает описание на основе типа;</li>
 *   <li>связан с бронированиями и отчётами (инцидентами).</li>
 * </ul>
 * </p>
 * <p>
 * Поддерживает автоматическое управление временными метками создания и обновления.
 * Поле {@code isAvailable} по умолчанию установлено в {@code true}.
 * </p>
 */
@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    /**
     * Уникальный идентификатор номера.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текстовый номер комнаты (например: "101", "A2", "ЛЮКС-3").
     * Обязательное, уникальное поле, до 10 символов.
     */
    @Column(name = "number", nullable = false, length = 10, unique = true)
    private String number;

    /**
     * Тип номера: определяет категорию (стандарт, люкс и т.д.).
     * Хранится как строка. Обязательное поле.
     */
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private RoomType type;

    /**
     * Описание типа номера (например: "Двухместный номер с видом на море").
     * Автоматически заполняется из {@link RoomType#getDescription()} при сохранении или обновлении.
     */
    @Column(name = "description",length = 100)
    private String description;

    /**
     * Максимальное количество гостей, которое может разместиться в номере.
     * Обязательное поле.
     */
    @Column(nullable = false)
    private Integer capacity;

    /**
     * Стоимость проживания за одну ночь в рублях.
     * Хранится с точностью до 2 знаков после запятой.
     */
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    /**
     * Флаг доступности номера для бронирования.
     * По умолчанию — {@code true}. Может быть изменён вручную (например, при ремонте).
     */
    @Column(name = "is_available", columnDefinition = "boolean default true")
    private Boolean isAvailable;

    /**
     * Дата и время добавления номера в систему.
     * Устанавливается автоматически при первом сохранении и не изменяется.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления данных о номере.
     * Обновляется автоматически при каждом изменении.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Список бронирований, связанных с этим номером.
     * Каскадное удаление не включено (бронирования могут существовать независимо).
     * Загружается лениво (LAZY).
     */
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Booking> bookings = new HashSet<>();

    /**
     * Список отчётов (инцидентов, запросов), связанных с этим номером.
     * Каскадное удаление не включено.
     * Загружается лениво (LAZY).
     */
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Report> reports = new HashSet<>();

    /**
     * Колбэк JPA: автоматически устанавливает поле {@code description}
     * на основе описания из перечисления {@link RoomType}
     * перед сохранением или обновлением сущности.
     */
    @PrePersist
    @PreUpdate
    private void preUpdate() {
        if (type != null) {
            this.description = type.getDescription();
        }
    }
}
