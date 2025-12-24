package com.asv.hotel.repositories;

import com.asv.hotel.entities.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode,Long> {

    @Query(value = "SELECT * FROM promo_codes WHERE code ILIKE :code AND is_active=true",nativeQuery = true)
    Optional<PromoCode> findActivePromoCodeByCode(@Param("code") String code);

    @Query(value = "SELECT * FROM promo_codes WHERE code ILIKE :code AND is_active=false",nativeQuery = true)
    Optional<PromoCode> findNotActivePromoCodeByCode(@Param("code") String code);

    @Query(value = "SELECT * FROM promo_codes WHERE code ILIKE :code",nativeQuery = true)
    Optional<PromoCode> findPromoCodesByCode(@Param("code") String code);

    @Modifying
    @Query(value = "DELETE FROM promo_codes WHERE code ILIKE :code", nativeQuery = true)
    int deleteByCode(@Param("code") String code);

}
