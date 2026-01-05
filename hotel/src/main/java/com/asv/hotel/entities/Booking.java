package com.asv.hotel.entities;

import com.asv.hotel.entities.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/**
 * Сущность бронирования номера в отеле.
 * <p>
 * Представляет запись о бронировании, включая:
 * <ul>
 *   <li>даты заезда и выезда;</li>
 *   <li>количество гостей;</li>
 *   <li>общую стоимость;</li>
 *   <li>статус бронирования и его текстовое описание;</li>
 *   <li>связь с комнатой, пользователем, промокодом;</li>
 *   <li>дополнительные сервисы, гостей и уведомления.</li>
 * </ul>
 * </p>
 * <p>
 * Поддерживает:
 * <ul>
 *   <li>автоматическое обновление временных меток {@code createdAt} и {@code updatedAt};</li>
 *   <li>синхронизацию {@code statusDescription} с описанием из {@link BookingStatus} при сохранении;</li>
 *   <li>именованный граф сущности {@code Booking.withServices} для загрузки связанных сервисов.</li>
 * </ul>
 * </p>
 * <p>
 * Использует стратегию генерации ID {@code IDENTITY} (автоинкремент в БД).
 * </p>
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
        name = "Booking.withServices",
        attributeNodes = {
                @NamedAttributeNode("serviceSet")
        }
)
public class Booking {

    /**
     * Уникальный идентификатор бронирования.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата заезда. Обязательное поле.
     */
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    /**
     * Дата выезда. Обязательное поле.
     */
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;


    /**
     * Количество гостей (персон). Обязательное поле.
     */
    @Column(name = "persons", nullable = false, scale = 0)
    private Integer persons;

    /**
     * Общая стоимость бронирования с учётом всех скидок и доп. услуг.
     * Хранится с точностью до 2 знаков после запятой.
     */
    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Текущий статус бронирования (например: CONFIRMED, CANCELLED, COMPLETED).
     * Используется строковое представление перечисления.
     */
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private BookingStatus statusOfBooking;

    /**
     * Текстовое описание статуса бронирования.
     * Автоматически заполняется из {@link BookingStatus#getDescription()} при сохранении или обновлении.
     * Не обновляется вручную после первоначальной установки.
     */
    @Column(name = "status_description", length = 100,updatable = false)
    private String statusDescription;

    /**
     * Дата и время создания записи. Устанавливается автоматически при первом сохранении.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления записи. Обновляется автоматически при каждом изменении.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Номер отеля, связанный с бронированием.
     * Обязательная связь. Загружается всегда (EAGER).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /**
     * Пользователь, создавший бронирование.
     * Обязательная связь. Загружается всегда (EAGER).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    /**
     * Промокод, применённый к бронированию (может отсутствовать).
     * Загружается всегда (EAGER).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;


    /**
     * Набор дополнительных сервисов, добавленных к бронированию.
     * Связь многие-ко-многим через таблицу {@code booking_service}.
     * Загружается лениво (LAZY).
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "booking_service",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<ServiceHotel> serviceSet;


    /**
     * Список уведомлений, связанных с этим бронированием.
     * Каскадное удаление и обновление. Удаление "сирот" включено.
     */
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationHotel> notificationHotels = new ArrayList<>();

    /**
     * Список гостей, прикреплённых к бронированию.
     * Хранится в отдельной таблице {@code booking_guests}.
     * Загружается всегда (EAGER).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "booking_guests",
            joinColumns = @JoinColumn(name = "booking_id")
            )
    private List<Guest> guestList;


    /**
     * Колбэк JPA: синхронизирует {@code statusDescription} с описанием из перечисления {@link BookingStatus}
     * перед сохранением или обновлением сущности.
     */
    @PrePersist
    @PreUpdate
    private void preUpdate() {
        if (statusOfBooking != null) {
            this.statusDescription = statusOfBooking.getDescription();
        }
    }

}
