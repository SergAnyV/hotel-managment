package com.asv.hotel.repositories;

import com.asv.hotel.entities.ServiceHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для управления дополнительными сервисами отеля ({@link ServiceHotel}).
 * <p>
 * Предоставляет методы для поиска сервиса по названию и его удаления.
 * </p>
 * <p>
 * Запросы реализованы через native SQL с поддержкой регистронезависимого поиска.
 * </p>
 */
@Repository
public interface ServiceHotelRepository extends JpaRepository<ServiceHotel, Long> {

    /**
     * Находит сервис по названию (регистронезависимо, частичное совпадение).
     * <p>
     * Возвращает не более одной записи благодаря {@code LIMIT 1}.
     * Поскольку поле {@code title} объявлено как {@code unique} в сущности,
     * ожидается максимум одно совпадение.
     * </p>
     *
     * @param title название сервиса или его часть (например: "завтрак", "спа")
     * @return {@link Optional} с найденным сервисом или пустой, если не найден
     */
    @Query(value = "SELECT * FROM services WHERE title ILIKE :title LIMIT 1", nativeQuery = true)
    Optional<ServiceHotel> findByTitle(@Param("title") String title);

    /**
     * Удаляет сервис по точному совпадению названия (регистрозависимо).
     * <p>
     * ⚠️ Обратите внимание: в отличие от метода поиска, здесь используется точное сравнение
     * ({@code =}), а не регистронезависимое ({@code ILIKE}).
     * </p>
     *
     * @param title точное название сервиса (регистр имеет значение)
     * @return количество удалённых строк (обычно 0 или 1)
     */
    @Modifying
    @Query(value = "DELETE FROM services WHERE title = :title", nativeQuery = true)
    int deleteByTitle(@Param("title") String title);
}
