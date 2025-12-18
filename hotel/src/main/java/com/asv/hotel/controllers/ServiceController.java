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

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Tag(name = "Service Management", description = "REST API для управления дополнительными сервисами")
@Validated
public class ServiceController {
    private final ServiceHotelService serviceHotelService;

    @Operation(summary = "Получить все сервисы в отеле",
            description = "Возвращает список всех сервисов включая неактивные")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @GetMapping
    public ResponseEntity<List<ServiceHotelDTO>> getAll() {
        return ResponseEntity.ok(serviceHotelService.findAllHotelServices());
    }

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

    @Operation(summary = "Создать новый тип service",
            description = "создает новый тип service")
    @ApiResponse(responseCode = "201", description = "тип service создан")
    @ApiResponse(responseCode = "409", description = "тип service не создан")
    @PostMapping
    public ResponseEntity<ServiceHotelDTO> createServiceHotel(@RequestBody @Valid ServiceHotelDTO serviceHotelDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceHotelService.createServiceHotel(serviceHotelDTO));
    }

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
