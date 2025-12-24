package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.bookingdto.BookingDTO;
import com.asv.hotel.dto.bookingdto.BookingSimplDTO;
import com.asv.hotel.entities.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mapper для преобразования сущности {@link Booking} и её DTO-представлений.
 * <p>
 * Использует библиотеку MapStruct для генерации эффективной
 * конвертации между доменной моделью и объектами передачи данных (DTO).
 * </p>
 * <p>
 * Подключает вспомогательные мапперы:
 * <ul>
 *   <li>{@link PromoCodeMapper} — для преобразования промокодов;</li>
 *   <li>{@link UserMapper} — для преобразования пользователей;</li>
 *   <li>{@link RoomMapper} — для преобразования номеров.</li>
 * </ul>
 * </p>
 * <p>
 * Содержит кастомные правила маппинга, включая извлечение вложенных полей.
 * </p>
 */
@Mapper(uses = {PromoCodeMapper.class, UserMapper.class, RoomMapper.class})
public interface BookingMapper {
    /**
     * Экземпляр маппера, созданный через фабрику MapStruct.
     * <p>
     * Предоставляет доступ к сгенерированным методам без необходимости внедрения через Spring.
     * </p>
     */
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    /**
     * Преобразует сущность {@link Booking} в полный DTO {@link BookingDTO}.
     * <p>
     * Особенности маппинга:
     * <ul>
     *   <li>Поле {@code createdAt} (LocalDateTime) преобразуется в {@code LocalDate} с помощью кастомного метода;</li>
     *   <li>Код промокода извлекается как {@code promoCode.code};</li>
     *   <li>Номер комнаты и данные пользователя маппятся через вложенные источники;</li>
     *   <li>Множество услуг {@code serviceSet} преобразуется в {@code serviceHotelDTOS}.</li>
     * </ul>
     * </p>
     *
     * @param booking сущность бронирования
     * @return DTO с полной информацией о бронировании
     */
    @Mapping(target = "createdAt", qualifiedByName = "mapLocalDateTimeToLocalDate")
    @Mapping(target = "promoCodeDTO", source = "promoCode.code")
    @Mapping(target = "roomSimpleDTO", source = "room")
    @Mapping(target = "userSimpleDTO", source = "user")
    @Mapping(target = "serviceHotelDTOS", source = "serviceSet")
    BookingDTO bookingToBookingDTO(Booking booking);

    /**
     * Преобразует упрощённый DTO {@link BookingSimplDTO} (входящий от клиента)
     * в сущность {@link Booking} для сохранения в БД.
     * <p>
     * Большинство полей (например, пользователь, номер, услуги)
     * должны быть дополнены вручную в сервисном слое, так как этот метод генерируется
     * только на основе совпадающих имён полей.
     * </p>
     *
     * @param bookingSimplDTO входящий DTO с данными бронирования
     * @return сущность бронирования (частично заполненная)
     */
    Booking bookingDTOToBooking(BookingSimplDTO bookingSimplDTO);

    /**
     * Преобразует сущность {@link Booking} в упрощённый DTO {@link BookingSimplDTO}.
     * <p>
     * Используется, например, при поиске бронирований по номеру комнаты.
     * Извлекает:
     * <ul>
     *   <li>номер комнаты из вложенного объекта {@code room.number};</li>
     *   <li>данные пользователя;</li>
     *   <li>код промокода.</li>
     * </ul>
     * </p>
     *
     * @param booking сущность бронирования
     * @return упрощённый DTO с основными данными
     */
    @Mapping(target = "roomNumber", source = "room.number")
    @Mapping(target = "userSimpleDTO", source = "user")
    @Mapping(target = "promoCodeDTO", source = "promoCode.code")
    BookingSimplDTO bookingToBookingSimpleDTO(Booking booking);

    /**
     * Преобразует упрощённый DTO {@link BookingSimplDTO} в полный DTO {@link BookingDTO}.
     * <p>
     * Может использоваться для подготовки ответа на основе входящих данных,
     * например, при предварительном расчёте стоимости.
     * </p>
     *
     * @param bookingSimplDTO входящий DTO
     * @return полный DTO (без идентификатора и некоторых серверных полей)
     */
    BookingDTO bookingSimpleToBookingDTO(BookingSimplDTO bookingSimplDTO);

    /**
     * Метод преобразования {@link BookingSimplDTO} → {@link Booking}.
     * <p>
     * Явно указывает маппинг поля {@code serviceSet}, что может быть необходимо
     * при нестандартной структуре данных.
     * </p>
     *
     * @param bookingSimplDTO входящий DTO
     * @return сущность бронирования
     */
    @Mapping(target = "serviceSet", source = "serviceSet")
    Booking bookingSimpleDTOToBooking(BookingSimplDTO bookingSimplDTO);

    /**
     * Кастомный метод преобразования {@link LocalDateTime} → {@link LocalDate}.
     * @param createdAt дата и время создания
     * @return только дата (без времени)
     */
    @Named("mapLocalDateTimeToLocalDate")
    default LocalDate mapLocalDateTimeToLocalDate(LocalDateTime createdAt) {
        return LocalDate.now();
    }
}
