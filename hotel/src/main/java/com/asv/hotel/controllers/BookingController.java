package com.asv.hotel.controllers;


import com.asv.hotel.dto.bookingdto.BookingDTO;
import com.asv.hotel.dto.bookingdto.BookingSimplDTO;

import com.asv.hotel.dto.bookingdto.ResponseBookingDTO;
import com.asv.hotel.dto.roomdto.RoomSimpleDataBaseDTO;
import com.asv.hotel.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Reader;
import java.time.LocalDate;
import java.util.List;
/**
 * Контроллер для управления бронированиями номеров в отеле.
 * <p>
 * Предоставляет RESTful эндпоинты для:
 * <ul>
 *   <li>создания нового бронирования;</li>
 *   <li>удаления бронирования по идентификатору;</li>
 *   <li>поиска всех бронирований по номеру комнаты;</li>
 *   <li>поиска свободных комнат в заданный период;</li>
 *   <li>получения детальной информации о бронировании по его ID.</li>
 * </ul>
 * </p>
 * <p>
 * Все входные параметры проходят валидацию с помощью аннотаций Jakarta Bean Validation.
 * Контроллер интегрирован со Swagger/OpenAPI для автоматической генерации документации.
 * </p>
 *
 * @see BookingService
 * @see BookingSimplDTO
 * @see BookingDTO
 * @see ResponseBookingDTO
 * @see RoomSimpleDataBaseDTO
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking Management", description = "REST API для управления бронирования номерами отеля")
@Validated
public class BookingController {
    private final BookingService bookingService;

    /**
     * Создаёт новое бронирование на основе упрощённых данных.
     *
     * @param bookingSimplDTO DTO с информацией о бронировании (обязательное поле, проходит валидацию)
     * @return {@link ResponseEntity} с полным объектом {@link BookingDTO} и статусом {@code 201 CREATED}
     * @throws jakarta.validation.ValidationException если входные данные не прошли валидацию
     */
    @Operation(summary = "Создать новое бронирование",
            description = "создает новое бронирование")
    @ApiResponse(responseCode = "201", description = "бронирование создано")
    @ApiResponse(responseCode = "409", description = "бронирование не создано")
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody @Valid BookingSimplDTO bookingSimplDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(bookingSimplDTO));
    }

    /**
     * Удаляет бронирование по его уникальному идентификатору.
     *
     * @param id идентификатор бронирования (обязательный, должен быть положительным)
     * @return {@link ResponseEntity} со статусом {@code 204 NO CONTENT} при успешном удалении
     * @throws com.asv.hotel.exceptions.HotelEntityNotFoundException если бронирование с указанным ID не найдено
     */
    @Operation(summary = "Удалить бронирование",
            description = "удаляет данные существующего бронирования по id")
    @ApiResponse(responseCode = "204", description = "бронирование удалено")
    @ApiResponse(responseCode = "404", description = "бронирование не найдено")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable
            @NotNull
            Long id) {
        bookingService.deleteBookingById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает список всех бронирований для комнаты с заданным номером.
     *
     * @param number номер комнаты (обязательный, непустой, содержит только буквы и цифры)
     * @return {@link ResponseEntity} со списком {@link BookingSimplDTO} и статусом {@code 200 OK}
     * @throws jakarta.validation.ConstraintViolationException если номер комнаты не соответствует формату
     */
    @Operation(summary = "Найти бронирование по номеру комнаты",
            description = "Возвращает данные бронирования по номеру комнаты")
    @ApiResponse(responseCode = "200", description = "бронирования найдены")
    @ApiResponse(responseCode = "404", description = "бронирования не найдены")
    @GetMapping("/room/{number}")
    public ResponseEntity<List<BookingSimplDTO>> getAllBookingsByRoomNumber(
            @PathVariable
            @NotBlank(message = "номер комнаты не должен быть пустым")
            @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$", message = "Комната может содержать только буквы, цифры ")
            String number) {
        return ResponseEntity.ok(bookingService.findAllBookingsSimpleDTOByRoomNumber(number));
    }
    /**
     * Возвращает список свободных комнат в указанный период дат.
     *
     * @param checkin дата заезда (должна быть сегодняшней или в будущем)
     * @param checkOut дата выезда (должна быть строго в будущем и позже даты заезда)
     * @return {@link ResponseEntity} со списком {@link RoomSimpleDataBaseDTO} и статусом {@code 200 OK},
     *         либо {@code 400 BAD REQUEST}, если свободных комнат нет
     * @implNote В текущей реализации возвращается статус 400, если список пуст.
     *          Возможно, логичнее вернуть пустой список со статусом 200.
     */
    @Operation(summary = "Найти свободные комнаты по датам бронирования",
            description = "Возвращает список свободных комнат на заданные даты")
    @ApiResponse(responseCode = "200", description = "бронирования найдены")
    @ApiResponse(responseCode = "404", description = "бронирования не найдены")
    @GetMapping("/date")
    public ResponseEntity<List<RoomSimpleDataBaseDTO>> getFreeRoomsBetweenDates(@RequestParam
                                                                                @FutureOrPresent
                                                                                @NotNull
                                                                                LocalDate checkin,
                                                                                @RequestParam
                                                                                @Future
                                                                                @NotNull
                                                                                LocalDate checkOut) {
        List<RoomSimpleDataBaseDTO> roomSimpleDataBaseDTOList = bookingService.findRoomSimpleDataBaseDTOByBookingDate(checkin, checkOut);
        if (roomSimpleDataBaseDTOList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(roomSimpleDataBaseDTOList);
    }

    @GetMapping("/booking/{bookingid}")
    public  ResponseEntity<ResponseBookingDTO> getBookingById(@PathVariable("bookingid")
                                                               @NotNull (message = "id бронирования не может быть null")
                                                               @Positive(message = "id бронирования должно быть положительна")
                                                               Long id){
        ResponseBookingDTO responseBookingDTO=bookingService.findResponseBookingDTOByBookingId(id);
        return ResponseEntity.ok(responseBookingDTO);
    }
}
