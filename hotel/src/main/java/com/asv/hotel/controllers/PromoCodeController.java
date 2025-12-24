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

@RestController
@RequestMapping("/promo-codes")
@RequiredArgsConstructor
@Tag(name = "Promo Code Management", description = "REST API для управления промокодами")
@Validated
public class PromoCodeController {
    private final PromoCodeService promoCodeService;

    @Operation(summary = "Создать новый промокод",
            description = "создает новый промокод")
    @ApiResponse(responseCode = "201", description = "промокод создан")
    @ApiResponse(responseCode = "409", description = "промокод не создан")
    @PostMapping
    ResponseEntity<PromoCodeDTO> createPromoCode(@RequestBody @Valid PromoCodeDTO promoCodeDTO) {
        PromoCodeDTO promoCodeDTOnew = promoCodeService.createPromoCode(promoCodeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(promoCodeDTOnew);
    }

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
    @Operation(summary = "вернуть все промокоды ",
            description = "вернуть все промокоды ")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @GetMapping
    public ResponseEntity<List<PromoCodeDTO>> getAll() {
    return ResponseEntity.ok(promoCodeService.findAllPromoCodesDTO());
    }
}
