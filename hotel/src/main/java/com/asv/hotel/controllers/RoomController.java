package com.asv.hotel.controllers;


import com.asv.hotel.dto.roomdto.RoomDTO;
import com.asv.hotel.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Контроллер для управления номерами отеля.
 * <p>
 * Обеспечивает полный набор CRUD-операций над сущностью "номер":
 * <ul>
 *   <li>получение всех номеров;</li>
 *   <li>поиск номера по его уникальному текстовому идентификатору (номеру комнаты);</li>
 *   <li>создание нового номера;</li>
 *   <li>обновление данных существующего номера;</li>
 *   <li>удаление номера по его номеру.</li>
 * </ul>
 * </p>
 * <p>
 * Номер комнаты — это строковый идентификатор, который:
 * <ul>
 *   <li>не может быть пустым;</li>
 *   <li>ограничен длиной (определяется в DTO);</li>
 *   <li>может содержать только буквы (кириллица и латиница) и цифры.</li>
 * </ul>
 * </p>
 * <p>
 * Все входные DTO проходят валидацию с помощью Jakarta Bean Validation.
 * Контроллер интегрирован с OpenAPI (Swagger) для автоматической генерации документации.
 * </p>
 *
 * @see RoomDTO
 * @see RoomService
 */
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Management", description = "API для управления номерами отеля")
@Validated
public class RoomController {
    private final RoomService roomService;

    /**
     * Возвращает список всех номеров отеля.
     *
     * @return {@link ResponseEntity} со списком {@link RoomDTO} и статусом {@code 200 OK}
     *         (возвращается пустой список, если номера отсутствуют)
     */
    @Operation(summary = "Получить все номера",
            description = "Возвращает список всех номеров отеля")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.findAllRoomsDTO());
    }


    /**
     * Возвращает данные номера по его текстовому номеру (идентификатору).
     *
     * @param number номер комнаты (обязательный, непустой, содержит только буквы и цифры)
     * @return {@link ResponseEntity} с объектом {@link RoomDTO} и статусом {@code 200 OK},
     *         либо {@code 400 BAD REQUEST}, если номер не найден
     * @throws jakarta.validation.ConstraintViolationException если параметр {@code number} не соответствует формату
     * @implNote В текущей реализации при отсутствии номера возвращается статус 400.
     *          Рекомендуется использовать статус {@code 404 NOT FOUND} для лучшей семантики REST.
     */
    @Operation(summary = "Найти номер по номеру",
            description = "Возвращает данные номера по номеру комнаты")
    @ApiResponse(responseCode = "200", description = "Номер найден")
    @ApiResponse(responseCode = "400", description = "Неверный запрос")
    @GetMapping("/{number}")
    public ResponseEntity<RoomDTO> getRoomByNumber(
            @PathVariable
            @NotBlank(message = "номер комнаты не должен быть пустым")
            @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$", message = "Комната может содержать только буквы, цифры ")
            String number) {
        RoomDTO roomDTO = roomService.findRoomDTOByNumber(number);
        if (roomDTO==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(roomDTO);

    }

    /**
     * Создаёт новый номер отеля.
     *
     * @param roomDTO DTO с данными нового номера (обязательный, проходит валидацию)
     * @return {@link ResponseEntity} с созданным {@link RoomDTO} и статусом {@code 201 CREATED}
     * @throws jakarta.validation.ValidationException если данные в DTO не соответствуют правилам валидации
     */
    @Operation(summary = "Создать новый номер",
            description = "создает новый номер")
    @ApiResponse(responseCode = "201", description = "Номер создан")
    @ApiResponse(responseCode = "409", description = "Номер не создан")
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@RequestBody @Valid RoomDTO roomDTO) {
        RoomDTO newRoomDTO = roomService.createRoom(roomDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRoomDTO);
    }
    /**
     * Обновляет данные существующего номера.
     *
     * @param roomDTO DTO с обновлёнными данными (обязательный, проходит валидацию)
     * @return {@link ResponseEntity} с обновлённым {@link RoomDTO} и статусом {@code 200 OK}
     * @throws jakarta.validation.ValidationException если данные в DTO некорректны
     */
    @Operation(summary = "Обновить данные номера",
            description = "обновляет данные существующего номера")
    @ApiResponse(responseCode = "200", description = "Номер обновлен")
    @ApiResponse(responseCode = "404", description = "Номер не найден")
    @ApiResponse(responseCode = "409", description = "Конфликт данных")
    @PutMapping
    public ResponseEntity<RoomDTO> updateRoom(
            @RequestBody @Valid RoomDTO roomDTO) {
        RoomDTO updatedRoom = roomService.changeDataRoom(roomDTO);
        return ResponseEntity.ok(updatedRoom);
    }
    /**
     * Удаляет номер отеля по его текстовому номеру.
     *
     * @param number номер комнаты (обязательный, непустой, содержит только буквы и цифры)
     * @return {@link ResponseEntity} со статусом {@code 204 NO CONTENT} при успешном удалении
     */
    @Operation(summary = "Удалить номер",
            description = "удвляет данные существующего номера")
    @ApiResponse(responseCode = "204", description = "Номер удален")
    @ApiResponse(responseCode = "404", description = "Номер не найден")
    @DeleteMapping("/{number}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable
            @NotBlank(message = "номер комнаты не должен быть пустым")
            @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$", message = "Комната может содержать только буквы, цифры ")
            String number) {
        roomService.deleteRoomByNumber(number);
        return ResponseEntity.noContent().build();
    }

}
