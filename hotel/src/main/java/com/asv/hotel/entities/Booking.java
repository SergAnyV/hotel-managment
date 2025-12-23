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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "persons", nullable = false, scale = 0)
    private Integer persons;

    @Column(name = "total_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private BookingStatus statusOfBooking;

    @Column(name = "status_description", length = 100,updatable = false)
    private String statusDescription;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "booking_service",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<ServiceHotel> serviceSet;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationHotel> notificationHotels = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "booking_guests",
            joinColumns = @JoinColumn(name = "booking_id")
            )
    private List<Guest> guestList;

    @PrePersist
    @PreUpdate
    private void preUpdate() {
        if (statusOfBooking != null) {
            this.statusDescription = statusOfBooking.getDescription();
        }
    }

}
