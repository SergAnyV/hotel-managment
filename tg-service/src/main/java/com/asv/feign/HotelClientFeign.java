package com.asv.feign;

import com.asv.models.bookingdto.BookingDTO;
import com.asv.models.bookingdto.BookingSimplDTO;
import com.asv.models.bookingdto.ResponseBookingDTO;
import com.asv.models.roomdto.RoomDTO;
import com.asv.models.roomdto.RoomSimpleDataBaseDTO;
import com.asv.models.security.JWTAuthentication;
import com.asv.models.security.SignInRequest;
import com.asv.models.servicehoteldto.ServiceHotelDTO;
import com.asv.models.userdto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
/**
 * Feign-клиент для  эндпоинтов сервиса отеля.
 * Не требует авторизации.
 */
@FeignClient(name = "hotel")
public interface HotelClientFeign {

    /**
     * Feign эндпоинт для создания нового юзера.
     * Не требует авторизации.
     */
    @PostMapping("/users")
    ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO);

    /**
     * Feign эндпоинт для авторизации.
     * Не требует авторизации.
     */
    @PostMapping("/auth/signin")
    ResponseEntity<JWTAuthentication> signIn(@RequestBody SignInRequest request);

    /**
     * Feign эндпоинт для просмотра всех возможных сервисов.
     * Не требует авторизации.
     */
    @GetMapping("/services")
    ResponseEntity<List<ServiceHotelDTO>> getAll();

    /**
     * Feign эндпоинт для просмотра всех номеров.
     * Не требует авторизации.
     */
    @GetMapping("/rooms")
    ResponseEntity<List<RoomDTO>> getAllRooms();

    @GetMapping("/rooms/{number}")
    ResponseEntity<RoomDTO> getRoomByNumber(
            @PathVariable("number") String number);

    /**
     * Feign эндпоинт для просмотра определенного сервиса по имени сервиса.
     * Не требует авторизации.
     */
    @GetMapping("/services/{title}")
    ResponseEntity<ServiceHotelDTO> getByTitle(@PathVariable String title);

    /**
     * Feign эндпоинт для просмотра определенной комнаты по ее номеру.
     * Не требует авторизации.
     */
    @GetMapping("/bookings/date")
    ResponseEntity<List<RoomSimpleDataBaseDTO>> getFreeRoomsBetweenDates(@RequestParam
                                                                         String checkin,
                                                                         @RequestParam
                                                                         String checkOut);

    @AuthHeaderFeign
    @PutMapping("/users")
    ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO);

    @AuthHeaderFeign
    @PostMapping("/bookings")
    ResponseEntity<BookingDTO> createBooking(@RequestBody BookingSimplDTO bookingSimplDTO);

    @AuthHeaderFeign
    @DeleteMapping("/bookings/delete/{id}")
    ResponseEntity<Void> deleteById(@PathVariable String id);

    @AuthHeaderFeign
    @GetMapping("/bookings/booking/{bookingid}")
    ResponseEntity<ResponseBookingDTO> getBookingById(@PathVariable("bookingid") String id);


}
