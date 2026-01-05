package com.asv.hotel.controllers;

import com.asv.hotel.dto.usertypedto.UserTypeDTO;
import com.asv.hotel.services.UserTypeService;
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
 * Контроллер для управления типами пользователей (ролями) в системе.
 * <p>
 * Обеспечивает CRUD-операции над сущностью "тип пользователя":
 * <ul>
 *   <li>получение всех типов;</li>
 *   <li>поиск по названию роли (регистронезависимо);</li>
 *   <li>создание нового типа;</li>
 *   <li>удаление по названию роли;</li>
 *   <li>обновление данных существующего типа.</li>
 * </ul>
 * </p>
 * <p>
 * Название роли (тип пользователя):
 * <ul>
 *   <li>должно содержать от 3 до 100 символов;</li>
 *   <li>может включать буквы (кириллица и латиница), цифры, пробелы и дефисы.</li>
 * </ul>
 * </p>
 * <p>
 * Все входные DTO проходят валидацию через Jakarta Bean Validation.
 * Интеграция с OpenAPI (Swagger) обеспечивает автоматическую документацию API.
 * </p>
 *
 * @see UserTypeDTO
 * @see UserTypeService
 */
@RestController
@RequestMapping("/user-types")
@RequiredArgsConstructor
@Tag(name = "User Type Managment", description = "REST API для управления типом пользователей")
@Validated
public class UserTypeController {
    private final UserTypeService userTypeService;

    /**
     * Возвращает список всех типов пользователей (ролей), зарегистрированных в системе.
     *
     * @return {@link ResponseEntity} со списком {@link UserTypeDTO} и статусом {@code 200 OK}
     */
    @Operation(summary = "Получить все типы пользователей",
            description = "Возвращает список всех типов пользователей")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @GetMapping
    public ResponseEntity<List<UserTypeDTO>> getAllTypes() {
        return ResponseEntity.ok(userTypeService.findAllUserTypeDTOs());
    }

    /**
     * Возвращает тип пользователя по названию роли (поиск без учёта регистра).
     *
     * @param role название роли (длина 3–100 символов, допустимы буквы, цифры, пробелы и дефисы)
     * @return {@link ResponseEntity} с объектом {@link UserTypeDTO} и статусом {@code 200 OK}
     */
    @Operation(summary = "Найти тип юзера по названию роли не зависимо от регистра",
            description = "Возвращает данные типа юзера по названию типа ")
    @ApiResponse(responseCode = "200", description = "тип найден")
    @ApiResponse(responseCode = "404", description = "тип не найден")
    @GetMapping("/{role}")
    public ResponseEntity<UserTypeDTO> getTypeByRole(
            @PathVariable
            @Size(min = 3, max = 100, message = "количество символов 3-100")
            @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9\\s-]+$", message = "Роль может содержать только буквы,дефис, цифры и пробелы")
            String role) {
        UserTypeDTO userTypeDTO = userTypeService.findUserTypeDTOByType(role);
        return ResponseEntity.ok(userTypeDTO);
    }

    /**
     * Создаёт новый тип пользователя (роль).
     *
     * @param userTypeDTO DTO с данными нового типа (проходит валидацию)
     * @return {@link ResponseEntity} с созданным {@link UserTypeDTO} и статусом {@code 201 CREATED}
     */
    @Operation(summary = "Создать новый тип юзера",
            description = "создает новый тип юзера")
    @ApiResponse(responseCode = "201", description = "тип юзера создан")
    @ApiResponse(responseCode = "409", description = "тип юзера не создан")
    @PostMapping
    public ResponseEntity<UserTypeDTO> createUserType(@RequestBody @Valid UserTypeDTO userTypeDTO) {
        UserTypeDTO newUserTypeDTO = userTypeService.createUserType(userTypeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserTypeDTO);
    }

    /**
     * Удаляет тип пользователя по названию роли.
     *
     * @param role название роли (длина 3–100 символов, допустимые символы: буквы, цифры, пробелы, дефисы)
     * @return {@link ResponseEntity} со статусом {@code 204 NO CONTENT}
     */
    @Operation(summary = "Удалить данные тип юзера",
            description = "Удалить данные существующего тип юзера")
    @ApiResponse(responseCode = "204", description = "тип юзера Удален")
    @ApiResponse(responseCode = "404", description = "тип юзера не найден")
    @DeleteMapping("/{role}")
    public ResponseEntity<UserTypeDTO> deleteUserType(
            @PathVariable
            @Size(min = 3, max = 100, message = "количество символов 3-100")
            @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9\\s-]+$", message = "Роль может содержать только буквы,дефис, цифры и пробелы")
            String role) {
        userTypeService.deleteUserTypeByType(role);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет данные существующего типа пользователя.
     *
     * @param userTypeDTO обновлённые данные типа (проходят валидацию)
     * @return {@link ResponseEntity} с обновлённым {@link UserTypeDTO} и статусом {@code 200 OK}
     */
    @Operation(summary = "Обновить данные типа юзера",
            description = "обновляет данные существующего типа юзера")
    @ApiResponse(responseCode = "200", description = "типа юзера обновлен")
    @ApiResponse(responseCode = "404", description = "типа юзера не найден")
    @ApiResponse(responseCode = "409", description = "Конфликт данных")
    @PutMapping
    public ResponseEntity<UserTypeDTO> update(@RequestBody @Valid UserTypeDTO userTypeDTO) {
        UserTypeDTO updatedUserTypeDto = userTypeService.cahngeDataUserType(userTypeDTO);
        return ResponseEntity.ok(updatedUserTypeDto);
    }
}
