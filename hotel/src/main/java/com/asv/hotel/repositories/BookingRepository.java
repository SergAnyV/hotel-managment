package com.asv.hotel.repositories;

import com.asv.hotel.dto.roomdto.RoomSimpleDataBaseDTO;
import com.asv.hotel.entities.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link Booking}.
 * <p>
 * Расширяет стандартный {@link JpaRepository} и предоставляет кастомные запросы:
 * <ul>
 *   <li>проверка доступности номера на заданные даты;</li>
 *   <li>поиск бронирований по номеру комнаты;</li>
 *   <li>поиск свободных номеров в указанный период;</li>
 *   <li>удаление бронирования по ID;</li>
 *   <li>загрузка бронирования с сервисами по ID;</li>
 *   <li>проверка принадлежности бронирования пользователю по nickname.</li>
 * </ul>
 * </p>
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Проверяет, доступен ли номер для бронирования в указанный период.
     * <p>
     * Номер считается занятым, если существует хотя бы одно незавершённое бронирование
     * (статус не {@code CANCELLED}), пересекающееся по датам с запрашиваемым интервалом.
     * </p>
     *
     * @param roomId       идентификатор номера
     * @param checkInDate  дата заезда (включительно)
     * @param checkOutDate дата выезда (исключительно)
     * @return {@code true}, если номер свободен на указанные даты; иначе {@code false}
     */
    @Query(value = "SELECT CASE WHEN COUNT(b.id) = 0 THEN true ELSE false END " +
            "FROM bookings b " +
            "WHERE b.room_id = :roomId " +
            "AND b.status != 'CANCELLED' " +
            "AND (b.check_in_date < :checkOutDate AND b.check_out_date > :checkInDate)",
            nativeQuery = true)
    boolean isRoomAvailableForDates(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);

    /**
     * Удаляет бронирование по его идентификатору с помощью native SQL.
     *
     * @param id идентификатор бронирования
     * @return количество удалённых записей (обычно 0 или 1)
     */
    @Modifying
    @Query(value = "DELETE FROM bookings b WHERE b.id = :id", nativeQuery = true)
    int deleteBookingById(@Param("id") Long id);

    /**
     * Возвращает все бронирования, связанные с номером, имеющим указанный текстовый номер.
     * Результат отсортирован по дате заезда в порядке убывания (сначала самые свежие).
     *
     * @param roomNumber текстовый номер комнаты (например: "101", "ЛЮКС-3")
     * @return список бронирований, возможно пустой
     */
    @Query(value = """
            SELECT b.* 
            FROM bookings b
            LEFT JOIN rooms r ON b.room_id = r.id
            WHERE r.number = :roomNumber
            ORDER BY b.check_in_date DESC
            """, nativeQuery = true)
    List<Booking> findAllByRoomNumber(@Param("roomNumber") String roomNumber);


    /**
     * Возвращает список свободных номеров на указанный период.
     * <p>
     * Номер считается свободным, если у него нет активных (непересекающихся и неотменённых)
     * бронирований в заданном интервале.
     * </p>
     *
     * @param checkIn  дата заезда
     * @param checkOut дата выезда
     * @return список DTO {@link RoomSimpleDataBaseDTO} с данными о свободных номерах
     */
    @Query(value = """
            SELECT r.number, r.type, r.description, r.capacity, r.price_per_night
            FROM rooms r
            LEFT JOIN bookings b
                ON r.id = b.room_id
                AND b.check_in_date < :checkOut
                AND b.check_out_date > :checkIn
                AND b.status != 'CANCELLED'
            WHERE b.id IS NULL;
            """, nativeQuery = true)
    List<RoomSimpleDataBaseDTO> findAllFreeRoomsBetweenDates(@Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);


    /**
     * Находит бронирование по ID и загружает связанные сервисы (доп. услуги) через именованный EntityGraph.
     *
     * @param id идентификатор бронирования
     * @return {@link Optional} с бронированием или пустой, если не найдено
     */
    @EntityGraph("Booking.withServices")
    Optional<Booking> findById(Long id);


    /**
     * Находит бронирование по идентификатору и проверяет, принадлежит ли оно пользователю с указанным nickname.
     *
     * @param nickName  nickname пользователя (уникальный логин)
     * @param bookingId идентификатор бронирования
     * @return {@link Optional} с бронированием, если оно существует и принадлежит пользователю; иначе пустой
     */
    @Query(value = """
            SELECT b.* 
            FROM bookings b
            LEFT JOIN users u
            ON b.user_id=u.id
            WHERE b.id=:bookingId 
            AND u.nick_name=:nickName
            """, nativeQuery = true)
    Optional<Booking> findBookingByIDAndUser_NickName(@Param("nickName") String nickName, @Param("bookingId") Long bookingId);
}
