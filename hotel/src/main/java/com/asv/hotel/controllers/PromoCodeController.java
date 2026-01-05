package com.asv.hotel.controllers;

import com.asv.hotel.dto.promocodedto.PromoCodeDTO;
import com.asv.hotel.services.PromoCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Контроллер для управления промокодами в системе бронирования отеля.
 * <p>
 * Предоставляет RESTful эндпоинты для:
 * <ul>
 *   <li>создания нового промокода;</li>
 *   <li>удаления промокода по его текстовому коду;</li>
 *   <li>получения списка всех существующих промокодов.</li>
 * </ul>
 * </p>
 * <p>
 * Все входные данные проходят валидацию с использованием Jakarta Bean Validation.
 * Текст промокода ограничивается длиной до 20 символов и может содержать только
 * буквы (кириллица и латиница) и цифры.
 * </p>
 * <p>
 * Контроллер интегрирован с OpenAPI (Swagger) для автоматической генерации
 * интерактивной документации API.
 * </p>
 *
 * @see PromoCodeDTO
 * @see PromoCodeService
 */
@RestController
@RequestMapping("/promo-codes")
@RequiredArgsConstructor
@Tag(name = "Promo Code Management", description = "REST API для управления промокодами")
@Validated
public class PromoCodeController {
    private final PromoCodeService promoCodeService;
    /**
     * Создаёт новый промокод на основе предоставленного DTO.
     *
     * @param promoCodeDTO объект с данными промокода (обязательный, проходит валидацию на уровне DTO)
     * @return {@link ResponseEntity} с созданным {@link PromoCodeDTO} и HTTP-статусом {@code 201 CREATED}
     * @throws jakarta.validation.ValidationException если данные в DTO не соответствуют правилам валидации
     */
    @Operation(summary = "Создать новый промокод",
            description = "создает новый промокод")
    @ApiResponse(responseCode = "201", description = "промокод создан")
    @ApiResponse(responseCode = "409", description = "промокод не создан")
    @PostMapping
    ResponseEntity<PromoCodeDTO> createPromoCode(@RequestBody @Valid PromoCodeDTO promoCodeDTO) {
        PromoCodeDTO promoCodeDTOnew = promoCodeService.createPromoCode(promoCodeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(promoCodeDTOnew);
    }
    /**
     * Удаляет промокод по его текстовому коду.
     *
     * @param code текстовый код промокода (должен быть непустым, длиной до 20 символов, содержать только буквы и цифры)
     * @return {@link ResponseEntity} с HTTP-статусом {@code 204 NO CONTENT} при успешном удалении
     * @throws com.asv.hotel.exceptions.HotelEntityNotFoundException если промокод с указанным кодом не найден
     */
    @Operation(summary = "Удалить промокод",
            description = "удаляет данные существующего промокод")
    @ApiResponse(responseCode = "204", description = "промокод удален")
    @ApiResponse(responseCode = "404", description = "промокод не найден")
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(
            @PathVariable
            @Size(max = 20, message = "Длина промокода не должна превышать 20 символов")
            @Pattern(regexp ="^[а-яА-ЯёЁa-zA-Z0-9]+$", message = "Промокод может содержать только буквы, цифры ")
            String code) {
        promoCodeService.deletePromoCodeByCode(code);
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает список всех активных и неактивных промокодов в системе.
     *
     * @return {@link ResponseEntity} со списком {@link PromoCodeDTO} и HTTP-статусом {@code 200 OK}
     *         (возвращается пустой список, если промокоды отсутствуют)
     */
    @Operation(summary = "вернуть все промокоды ",
            description = "вернуть все промокоды ")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @GetMapping
    public ResponseEntity<List<PromoCodeDTO>> getAll() {
    return ResponseEntity.ok(promoCodeService.findAllPromoCodesDTO());
    }
}
