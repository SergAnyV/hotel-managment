package com.asv.hotel.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность дополнительного сервиса, предоставляемого отелем.
 * <p>
 * Описывает платную или бесплатную услугу (например: "Завтрак", "Парковка", "Спа").
 * Каждый сервис имеет уникальное название, описание, стоимость и может быть привязан
 * к одному или нескольким бронированиям.
 * </p>
 * <p>
 * Поддерживает автоматическое управление временными метками создания и обновления.
 * </p>
 */
@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceHotel {

    /**
     * Уникальный идентификатор сервиса.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название сервиса (например: "Завтрак в номер").
     * Обязательное, уникальное поле, до 50 символов.
     */
    @Column(name = "title", nullable = false, unique = true, length = 50)
    private String title;

    /**
     * Подробное описание сервиса (например: "Континентальный завтрак с 7:00 до 10:00").
     * Обязательное, уникальное поле, до 250 символов.
     */
    @Column(name = "description", nullable = false, unique = true, length = 250)
    private String description;

    /**
     * Стоимость услуги в рублях.
     * Обязательное поле с точностью до 2 знаков после запятой.
     * Для бесплатных сервисов значение может быть {@code 0.00}.
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Дата и время создания записи о сервисе.
     * Устанавливается автоматически при первом сохранении и не изменяется.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления данных о сервисе.
     * Обновляется автоматически при каждом изменении.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Набор бронирований, к которым был добавлен этот сервис.
     * Связь многие-ко-многим через таблицу {@code booking_service} (управляемую стороной {@link Booking}).
     * Загружается лениво (LAZY).
     * Инициализируется пустым {@link HashSet} для безопасной работы без null-проверок.
     */
    @ManyToMany(mappedBy = "serviceSet", fetch = FetchType.LAZY)
    private Set<Booking> bookingSet = new HashSet<>();
}
