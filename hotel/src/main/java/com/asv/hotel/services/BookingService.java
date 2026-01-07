package com.asv.hotel.services;

import com.asv.hotel.dto.bookingdto.BookingDTO;
import com.asv.hotel.dto.bookingdto.BookingSimplDTO;
import com.asv.hotel.dto.bookingdto.ResponseBookingDTO;
import com.asv.hotel.dto.roomdto.RoomSimpleDataBaseDTO;


import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для управления бронированиями номеров отеля.
 * <p>
 * Предоставляет методы для создания, удаления, поиска и получения детальной информации
 * о бронированиях, а также для поиска свободных номеров на заданные даты.
 * </p>
 */
public interface BookingService {
    /**
     * Создаёт новое бронирование на основе упрощённых данных.
     *
     * @param bookingSimplDTO данные для создания бронирования
     * @return DTO с полной информацией о созданном бронировании
     */
    BookingDTO createBooking(BookingSimplDTO bookingSimplDTO);

    /**
     * Удаляет бронирование по его уникальному идентификатору.
     *
     * @param id идентификатор бронирования
     */
    void deleteBookingById(Long id);

    /**
     * Возвращает все бронирования, связанные с указанным номером комнаты.
     *
     * @param roomNumber текстовый номер комнаты (например: "101", "ЛЮКС-3")
     * @return список упрощённых DTO бронирований
     */
    List<BookingSimplDTO> findAllBookingsSimpleDTOByRoomNumber(String roomNumber);

    /**
     * Возвращает детальную информацию о бронировании по его идентификатору.
     *
     * @param id идентификатор бронирования
     * @return DTO с полной информацией о бронировании
     */
    ResponseBookingDTO findResponseBookingDTOByBookingId(Long id);

    /**
     * Возвращает список свободных номеров на указанный период дат.
     *
     * @param checkInDate  дата заезда
     * @param checkOutDate дата выезда
     * @return список DTO свободных номеров
     */
    List<RoomSimpleDataBaseDTO> findRoomSimpleDataBaseDTOByBookingDate(LocalDate checkInDate, LocalDate checkOutDate);
}

