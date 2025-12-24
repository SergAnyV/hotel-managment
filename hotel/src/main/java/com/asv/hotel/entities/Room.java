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


@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, length = 10, unique = true)
    private String number;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private RoomType type;

    @Column(name = "description",length = 100)
    private String description;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "is_available", columnDefinition = "boolean default true")
    private Boolean isAvailable;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Report> reports = new HashSet<>();

    @PrePersist
    @PreUpdate
    private void preUpdate() {
        if (type != null) {
            this.description = type.getDescription();
        }
    }
}
