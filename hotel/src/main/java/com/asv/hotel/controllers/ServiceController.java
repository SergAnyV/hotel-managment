package com.asv.hotel.controllers;

import com.asv.hotel.dto.servicehoteldto.ServiceHotelDTO;
import com.asv.hotel.services.ServiceHotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Контроллер для управления дополнительными сервисами отеля (например: спа, парковка, завтрак и т.д.).
 * <p>
 * Предоставляет RESTful эндпоинты для:
 * <ul>
 *   <li>получения полного списка сервисов (включая неактивные);</li>
 *   <li>поиска сервиса по названию (регистронезависимо);</li>
 *   <li>создания нового типа сервиса;</li>
 *   <li>удаления сервиса по его названию.</li>
 * </ul>
 * </p>
 * <p>
 * Название сервиса:
 * <ul>
 *   <li>не может быть пустым;</li>
 *   <li>должно содержать от 3 до 20 символов;</li>
 *   <li>сравнение при поиске и удалении выполняется без учёта регистра.</li>
 * </ul>
 * </p>
 * <p>
 * Все входные DTO проходят валидацию через Jakarta Bean Validation.
 * Контроллер интегрирован с OpenAPI (Swagger) для генерации документации.
 * </p>
 *
 * @see ServiceHotelDTO
 * @see ServiceHotelService
 */
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Tag(name = "Service Management", description = "REST API для управления дополнительными сервисами")
@Validated
public class ServiceController {
    private final ServiceHotelService serviceHotelService;
    /**
     * Возвращает список всех зарегистрированных сервисов отеля (включая неактивные).
     *
     * @return {@link ResponseEntity} со списком {@link ServiceHotelDTO} и статусом {@code 200 OK}
     *         (может быть пустым, если сервисы отсутствуют)
     */
    @Operation(summary = "Получить все сервисы в отеле",
            description = "Возвращает список всех сервисов включая неактивные")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @GetMapping
    public ResponseEntity<List<ServiceHotelDTO>> getAll() {
        return ResponseEntity.ok(serviceHotelService.findAllHotelServices());
    }
    /**
     * Возвращает данные сервиса по его названию (поиск без учёта регистра).
     *
     * @param title название сервиса (обязательное, длина от 3 до 20 символов)
     * @return {@link ResponseEntity} с объектом {@link ServiceHotelDTO} и статусом {@code 200 OK}

     * @throws jakarta.validation.ConstraintViolationException если название не соответствует требованиям длины или пусто
     */
    @Operation(summary = "Найти service по названию  не зависимо от регистра",
            description = "Возвращает service по названию")
    @ApiResponse(responseCode = "200", description = "service найден")
    @ApiResponse(responseCode = "404", description = "service не найден")
    @GetMapping("/{title}")
    public ResponseEntity<ServiceHotelDTO> getByTitle(
            @PathVariable
            @NotBlank(message = " не должен быть пустым")
            @Size(min = 3, max = 20, message = "количество символов 3-20")
            String title) {
        return ResponseEntity.ok(serviceHotelService.findServiceHotelDTOByTitle(title));
    }
    /**
     * Создаёт новый тип дополнительного сервиса.
     *
     * @param serviceHotelDTO DTO с данными нового сервиса (обязательный, проходит валидацию)
     * @return {@link ResponseEntity} с созданным {@link ServiceHotelDTO} и статусом {@code 201 CREATED}
     * @throws jakarta.validation.ValidationException если данные в DTO не прошли валидацию
     */
    @Operation(summary = "Создать новый тип service",
            description = "создает новый тип service")
    @ApiResponse(responseCode = "201", description = "тип service создан")
    @ApiResponse(responseCode = "409", description = "тип service не создан")
    @PostMapping
    public ResponseEntity<ServiceHotelDTO> createServiceHotel(@RequestBody @Valid ServiceHotelDTO serviceHotelDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceHotelService.createServiceHotel(serviceHotelDTO));
    }
    /**
     * Удаляет сервис по его названию (сравнение без учёта регистра).
     *
     * @param title название сервиса (обязательное, длина от 3 до 20 символов)
     * @return {@link ResponseEntity} со статусом {@code 204 NO CONTENT} при успешном удалении
     * @throws com.asv.hotel.exceptions.HotelEntityNotFoundException если сервис не найден
     */
    @Operation(summary = "Удалить данные тип service",
            description = "Удалить данные существующего тип service")
    @ApiResponse(responseCode = "204", description = "тип service Удален")
    @ApiResponse(responseCode = "404", description = "тип service не найден")
    @DeleteMapping("/{title}")
    public ResponseEntity<ServiceHotelDTO> deleteByTitle(
            @PathVariable
            @NotBlank(message = " не должен быть пустым")
            @Size(min = 3, max = 20, message = "количество символов 3-20")
            String title) {
        serviceHotelService.deletServiceHotelByTtitle(title);
        return ResponseEntity.noContent().build();
    }


}
