package com.asv.hotel.repositories;

import com.asv.hotel.entities.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с промокодами ({@link PromoCode}).
 * <p>
 * Предоставляет методы для поиска промокодов по текстовому коду с учётом или без учёта активности,
 * а также для удаления по коду.
 * </p>
 * <p>
 * Все запросы реализованы через native SQL и используют регистронезависимое сравнение (ILIKE).
 * </p>
 */
@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    /**
     * Находит активный промокод по коду (регистронезависимо).
     *
     * @param code текстовый код промокода (например: "WELCOME10")
     * @return {@link Optional} с промокодом, если он существует и {@code is_active = true}; иначе пустой
     */
    @Query(value = "SELECT * FROM promo_codes WHERE code ILIKE :code AND is_active=true", nativeQuery = true)
    Optional<PromoCode> findActivePromoCodeByCode(@Param("code") String code);

    /**
     * Находит неактивный промокод по коду (регистронезависимо).
     *
     * @param code текстовый код промокода
     * @return {@link Optional} с промокодом, если он существует и {@code is_active = false}; иначе пустой
     */
    @Query(value = "SELECT * FROM promo_codes WHERE code ILIKE :code AND is_active=false", nativeQuery = true)
    Optional<PromoCode> findNotActivePromoCodeByCode(@Param("code") String code);

    /**
     * Находит любой промокод по коду (регистронезависимо), независимо от статуса активности.
     *
     * @param code текстовый код промокода
     * @return {@link Optional} с промокодом, если он существует; иначе пустой
     */
    @Query(value = "SELECT * FROM promo_codes WHERE code ILIKE :code", nativeQuery = true)
    Optional<PromoCode> findPromoCodesByCode(@Param("code") String code);

    /**
     * Удаляет промокод по коду (регистронезависимо).
     * <p>
     * Удаляет все записи, код которых совпадает с указанным (должна быть максимум одна,
     * так как поле {@code code} объявлено как {@code unique} в сущности).
     * </p>
     *
     * @param code текстовый код промокода
     * @return количество удалённых строк (обычно 0 или 1)
     */
    @Modifying
    @Query(value = "DELETE FROM promo_codes WHERE code ILIKE :code", nativeQuery = true)
    int deleteByCode(@Param("code") String code);

}
