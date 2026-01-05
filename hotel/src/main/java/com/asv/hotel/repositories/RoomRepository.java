package com.asv.hotel.repositories;

import com.asv.hotel.entities.Room;
import com.asv.hotel.entities.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями номеров отеля ({@link Room}).
 * <p>
 * Предоставляет методы для:
 * <ul>
 *   <li>поиска по номеру, типу и ценовому диапазону;</li>
 *   <li>удаления по номеру;</li>
 *   <li>полного обновления данных номера по ID.</li>
 * </ul>
 * </p>
 * <p>
 * Кастомные запросы реализованы через native SQL. Поиск по текстовым полям — регистронезависимый (ILIKE).
 * </p>
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Возвращает список всех номеров.
     * <p>
     * ⚠️ Метод дублирует унаследованный {@link JpaRepository#findAll()}.
     * Рекомендуется использовать унаследованный метод напрямую.
     * </p>
     *
     * @return список всех записей в таблице {@code rooms}
     */
    List<Room> findAll();

    /**
     * Находит номер отеля по частичному или полному совпадению текстового номера (регистронезависимо).
     *
     * @param number часть или полный номер комнаты (например: "101", "люкс")
     * @return {@link Optional} с номером или пустой, если не найден
     */
    @Query(value = "SELECT * FROM rooms WHERE number ILIKE :number ", nativeQuery = true)
    Optional<Room> findRoomByNumberLikeIgnoreCase(String number);

    /**
     * Находит все номера указанного типа (регистронезависимо).
     * <p>
     * ⚠️ Передача enum-значения в native-запрос через {@code ILIKE} может работать некорректно,
     * так как enum сериализуется в строку, но сравнение зависит от СУБД.
     * </p>
     *
     * @param type тип номера (например: STANDARD, LUXURY)
     * @return список номеров указанного типа (может быть пустым)
     */
    @Query(value = "SELECT * FROM rooms WHERE type ILIKE :type ", nativeQuery = true)
    List<Room> findRoomByTypeLikeIgnoreCase(@Param("type") RoomType type);

    /**
     * Находит номера, стоимость за ночь которых находится в указанном диапазоне (включительно).
     *
     * @param min минимальная цена
     * @param max максимальная цена
     * @return список номеров в ценовом диапазоне
     */
    @Query(value = "SELECT * FROM rooms WHERE pricePerNight BETWEEN :min AND :max", nativeQuery = true)
    List<Room> findRoomByPricePerNightBetween(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    /**
     * Удаляет номер отеля по частичному или полному совпадению текстового номера (регистронезависимо).
     *
     * @param number часть или полный номер комнаты
     * @return количество удалённых строк (обычно 0 или 1, так как {@code number} — уникальное поле)
     */
    @Modifying
    @Query(value = "DELETE FROM rooms WHERE number ILIKE :number", nativeQuery = true)
    int deleteRoomByNumberLikeIgnoreCase(@Param("number") String number);

    /**
     * Полностью обновляет данные номера по его идентификатору.
     * <p>
     * Обновляются все основные поля: номер, тип, описание, вместимость, цена и статус доступности.
     * </p>
     *
     * @param roomId        идентификатор номера
     * @param number        новый текстовый номер
     * @param type          новый тип номера
     * @param description   новое описание
     * @param capacity      новая вместимость
     * @param pricePerNight новая стоимость за ночь
     * @param isAvailable   новый статус доступности
     * @return количество обновлённых строк (обычно 1 при успехе)
     */
    @Modifying
    @Query(value = """
            UPDATE rooms SET number = :number, type = :type, description = :description, 
            capacity = :capacity, price_per_night = :pricePerNight, is_available = :isAvailable WHERE id = :roomId
            """, nativeQuery = true)
    int updateRoom(@Param("roomId") Long roomId, @Param("number") String number, @Param("type") RoomType type,
                   @Param("description") String description, @Param("capacity") Integer capacity,
                   @Param("pricePerNight") BigDecimal pricePerNight, @Param("isAvailable") Boolean isAvailable);

}
