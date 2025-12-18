package com.asv.hotel.dto.bookingdto;

import com.asv.hotel.dto.servicehoteldto.ServiceHotelSimpleDTO;
import com.asv.hotel.entities.Guest;
import com.asv.hotel.entities.enums.BookingStatus;
import com.asv.hotel.entities.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
/**
 * DTO для ответа клиенту с полной информацией о бронировании.
 * <p>
 * Используется в качестве возвращаемого значения в REST-контроллере при запросе
 * деталей конкретного бронирования (например, по его идентификатору).
 * Содержит агрегированную информацию о брони, включая данные о номере,
 * пользователе, стоимости, статусе и подключённых услугах.
 * </p>
 * <p>
 * Данный DTO предназначен исключительно для <strong>чтения</strong> (read-only)
 * и не должен использоваться для приёма данных от клиента.
 * </p>
 * <p>
 * Объект возвращается только авторизованному пользователю, на которого оформлено
 * бронирование, либо администратору/менеджеру (согласно логике).
 * </p>
 *
 * @see com.asv.hotel.controllers.BookingController#getBookingById
 * @see com.asv.hotel.services.implementations.BookingServiceImpl#findResponseBookingDTOByBookingId
 * @see ServiceHotelSimpleDTO
 * @see BookingStatus
 * @see RoomType
 */
@Schema(description = "Информативная модель данных по бронированию возвращаемая пользователю при запросе ")
@Builder
@Getter
public class ResponseBookingDTO {
    /**
     * Уникальный идентификатор бронирования.
     */
    @Schema(description = "Уникальный номер бронирования")
    private Long bookingId;

    /**
     * Дата заезда (начало проживания).
     */
    @Schema(description = "дата начала бронирования")
    private LocalDate checkInDate;
    /**
     * Дата выезда (окончание проживания).
     */
    @Schema(description = "дата окончания бронирования")
    private LocalDate checkOutDate;
    /**
     * Количество человек, указанных при бронировании.
     */
    @Schema(description = "количество проживающих")
    private Integer persons;
    /**
     * Итоговая стоимость бронирования, включая номер и все подключённые услуги,
     * с учётом применённого промокода (если был).
     */
    @Schema(description = "полная стоимость проживания")
    private BigDecimal totalPrice;
    /**
     * Текущий статус бронирования (например, {@code CONFIRMED}, {@code CANCELLED} и т.д.).
     *
     * @see BookingStatus
     */
    @Schema(description = "статус бронирования")
    private BookingStatus statusOfBooking;
    /**
     * Номер забронированной комнаты (например, "101", "A205").
     */
    @Schema(description = "номер комнаты")
    private String roomNumber;
    /**
     * Тип забронированного номера (например, {@code STANDARD}, {@code LUXURY},{@code ECONOM}).
     *
     * @see RoomType
     */
    @Schema(description = "тип комнаты")
    private RoomType type;
    /**
     * Человекочитаемое описание типа комнаты (например, "Стандартный двухместный номер").
     * Значение берётся из перечисления {@link RoomType#getDescription()}.
     */
    @Schema(description = "пояснение типа комнаты")
    private String descriptionTypeOfRoom;
    /**
     * Имя пользователя, на которого оформлено бронирование.
     */
    @Schema(description = "Имя пользователя на кого создана бронь")
    private String firstName;
    /**
     * Фамилия пользователя, на которого оформлено бронирование.
     */
    @Schema(description = "Фамилия пользователя на кого создана бронь")
    private String lastName;
    /**
     * Электронная почта пользователя, на которого оформлено бронирование.
     */
    @Schema(description = "Email пользователя на кого создана бронь")
    private String email;
    /**
     * Номер телефона пользователя, на которого оформлено бронирование.
     */
    @Schema(description = "Номер телефона пользователя на кого создана бронь")
    private String phoneNumber;

    /**
     * Множество дополнительных услуг отеля, выбранных при бронировании.
     * <p>
     * Каждая услуга представлена упрощённым DTO {@link ServiceHotelSimpleDTO}.
     * Может быть пустым или {@code null}, если услуги не выбраны.
     * </p>
     */
    @Schema(description = "сервисы")
    private Set<ServiceHotelSimpleDTO> serviceHotelSimpleDTOS;

    /**
     * Список гостей, которые будут проживать в забронированном номере.
     * <p>
     * Содержит полные сущности {@link Guest}.Вносятся проживающие в данном номере.
     * </p>
     */
    @Schema(description = "список гостей проживающих в номере")
    private List<Guest> guestList;
}
