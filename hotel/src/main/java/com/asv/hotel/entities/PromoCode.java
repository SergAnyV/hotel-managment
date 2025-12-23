package com.asv.hotel.entities;

import com.asv.hotel.entities.enums.TypeOfPromoCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "discount_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TypeOfPromoCode typeOfPromoCode;

    @Column(name="description",length = 100)
    private String description;

    @Column(name = "discount_value", nullable = false, precision = 8, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFromDate;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntilDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @PrePersist
    @PreUpdate
    private void preUpdate() {
        if (typeOfPromoCode != null) {
            this.description = typeOfPromoCode.getDescription();
        }
    }

}
