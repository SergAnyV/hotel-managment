package com.asv.hotel.dto.bookingdto;

import com.asv.hotel.entities.Guest;
import com.asv.hotel.dto.roomdto.RoomSimpleDTO;
import com.asv.hotel.dto.servicehoteldto.ServiceHotelDTO;
import com.asv.hotel.dto.userdto.UserSimpleDTO;
import com.asv.hotel.entities.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
/**
 * Data Transfer Object (DTO), представляющий информацию о бронировании номера в отеле.
 * <p>
 * Используется для передачи данных между слоями приложения .
 * Содержит основные атрибуты бронирования.
 * </p>
 * <p>
 * Аннотирован с помощью Jakarta Bean Validation для обеспечения корректности входных данных,
 * а также содержит Swagger-аннотации для генерации документации OpenAPI.
 * </p>
 *
 * @see RoomSimpleDTO
 * @see UserSimpleDTO
 * @see ServiceHotelDTO
 * @see Guest
 * @see BookingStatus
 */
@Data
@Builder
public class BookingDTO {

    /**
     * Уникальный идентификатор бронирования.
     * <p>
     * Обязательное поле. Должно быть не {@code null}.
     * </p>
     */
    @Schema(description = "уникальный номер бронирования", example = "1L")
    @NotNull(message = "уникальный номер бронирования обязателен")
    private Long id;

    /**
     * Дата заезда гостя в отель.
     * <p>
     * Обязательное поле. Должна быть сегодняшней или будущей датой.
     * </p>
     */
    @Schema(description = "Дата заезда", example = "2023-06-01")
    @NotNull(message = "Дата начала проживания обязательна")
    @FutureOrPresent
    private LocalDate checkInDate;

    /**
     * Дата выезда гостя из отеля.
     * <p>
     * Обязательное поле. Должна быть строго в будущем относительно текущей даты.
     * </p>
     */
    @Schema(description = "Дата окончания действия", example = "2023-07-01")
    @NotNull(message = "Дата выезда обязательна")
    @Future
    private LocalDate checkOutDate;

    /**
     * Количество человек, проживающих в номере.
     * <p>
     * Должно быть в диапазоне от 1 до 10 включительно, в зависимости от выбранного номера.
     * </p>
     */
    @Schema(description = "количество человек", example = "2", type = "integer")
    @Min(value = 1, message = "Вместимость должна быть не менее 1")
    @Max(value = 10, message = "Вместимость должна быть не более 10")
    private Integer persons;
    /**
     * Общая стоимость бронирования.
     * <p>
     * Должна быть положительным числом (больше нуля).
     * </p>
     */
    @Schema(description = "общая сумма не должна быть 0 или отрицательная ", example = "1000")
    @Positive(message = "цена должна быть положительна")
    private BigDecimal totalPrice;

    /**
     * Текущий статус бронирования.
     * <p>
     * Обязательное поле. Возможные значения определены в перечислении {@link BookingStatus}.
     * </p>
     */
    @Schema(description = "статус (CONFIRMED и т.д.)",allowableValues = {"REQUEST", "CONFIRMED","CANCELLED"})
    @NotNull
    private BookingStatus statusOfBooking;

    /**
     * Дата создания записи о бронировании.
     * <p>
     * Поле доступно только для чтения (не должно передаваться клиентом при создании/обновлении).
     * Должна быть сегодняшней или прошедшей датой.
     * </p>
     */
    @Schema(description = "Дата создания записи", example = "2023-01-01T12:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    @PastOrPresent
    private LocalDate createdAt;

    /**
     * Информация о забронированном номере.
     * <p>
     * Представлена в упрощённом виде через {@link RoomSimpleDTO}.
     * </p>
     */
    @Schema(description = "Данные комнаты")
    private RoomSimpleDTO roomSimpleDTO;

    /**
     * Информация о пользователе, оформившем бронирование.
     * <p>
     * Представлена в упрощённом виде через {@link UserSimpleDTO}.
     * </p>
     */
    @Schema(description = "Данные пользователя")
    private UserSimpleDTO userSimpleDTO;

    /**
     * Название применённого промокода (если был использован).
     * <p>
     * Может быть {@code null}, если промокод не применялся.
     * </p>
     */
    @Schema(description = "Название промокода")
    private String promoCodeDTO;

    /**
     * Множество дополнительных услуг отеля, подключённых к бронированию.
     * <p>
     * Каждая услуга представлена объектом {@link ServiceHotelDTO}.
     * Может быть пустым или {@code null}.
     * </p>
     */
    @Schema(description = "подключенные сервисы")
    private Set<ServiceHotelDTO> serviceHotelDTOS ;
    /**
     * Список гостей, проживающих в забронированном номере.
     * <p>
     * Содержит полные сущности {@link Guest}. Вносятся проживающие в данном номере.
     * </p>
     */
    @Schema(description = "список гостей проживающих в номере")
    private List<Guest> guestList;

   }
