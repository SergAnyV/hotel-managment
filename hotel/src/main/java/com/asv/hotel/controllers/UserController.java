package com.asv.hotel.controllers;

import com.asv.hotel.dto.userdto.UserDTO;
import com.asv.hotel.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
/**
 * Контроллер для управления пользователями системы.
 * <p>
 * Обеспечивает операции:
 * <ul>
 *   <li>создания нового пользователя;</li>
 *   <li>поиска по имени и фамилии;</li>
 *   <li>поиска по номеру телефона (только для администраторов);</li>
 *   <li>удаления по имени и фамилии;</li>
 *   <li>обновления данных пользователя;</li>
 *   <li>подтверждения регистрации по токену через HTML-страницу.</li>
 * </ul>
 * </p>
 * <p>
 * Все строковые входные параметры проходят строгую валидацию:
 * <ul>
 *   <li>Имя и фамилия — только русские буквы и дефисы, длина от 3 до 50 символов;</li>
 *   <li>Номер телефона — только цифры, длина от 3 до 20 символов.</li>
 * </ul>
 * </p>
 * <p>
 * Доступ к некоторым эндпоинтам ограничен ролями (например, поиск по телефону доступен только ADMIN).
 * </p>
 *
 * @see UserDTO
 * @see UserService
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "REST API для управления юзерами")
public class UserController {
    private final UserService userService;
    /**
     * Создаёт нового пользователя на основе переданного DTO.
     *
     * @param userDTO данные нового пользователя (проходят валидацию)
     * @return {@link ResponseEntity} с созданным {@link UserDTO} и статусом {@code 201 CREATED}
     */
    @Operation(summary = "Создать нового юзера",
            description = "создает нового юзера")
    @ApiResponse(responseCode = "201", description = "юзера создан")
    @ApiResponse(responseCode = "409", description = "юзера не создан")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        UserDTO newuserDTO = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newuserDTO);
    }

    /**
     * Возвращает пользователя по его фамилии и имени.
     *
     * @param lastName  фамилия (только русские буквы и дефисы, 3–50 символов)
     * @param firstName имя (только русские буквы и дефисы, 3–50 символов)
     * @return {@link ResponseEntity} с объектом {@link UserDTO} и статусом {@code 200 OK}
     */
    @Operation(summary = "Получить по имени и фамилии",
            description = "Возвращает юзера")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @ApiResponse(responseCode = "404", description = "Номер не найден")
    @GetMapping("/by-name-surname")
    public ResponseEntity<UserDTO> getUserByLastNameAndFirstName(
            @RequestParam("lastName")
            @Size(min = 3, max = 50, message = "количество символов 3-50")
            @Pattern(
                    regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
                    message = "Фамилия может содержать только русские буквы, дефисы"
            )
            @NotBlank(message = "Фамилия пользователя, не должен быть пустым")
            String lastName,
            @RequestParam("firstName")
            @Size(min = 3, max = 50, message = "количество символов 3-50")
            @Pattern(
                    regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
                    message = "Имя может содержать только русские буквы, дефисы"
            )
            @NotBlank(message = "Имя пользователя, не должен быть пустым")
            String firstName) {
        return ResponseEntity.ok(userService.findUserDTOByLastNameAndFirstName(lastName, firstName));
    }

    /**
     * Возвращает пользователя по номеру телефона.
     * <p>
     * Доступ разрешён только пользователям с ролью {@code ADMIN}.
     *
     * @param phoneNumber номер телефона (только цифры, 3–20 символов)
     * @return {@link ResponseEntity} с объектом {@link UserDTO} и статусом {@code 200 OK}
     */
    @Operation(summary = "Получить по номеру телефона",
            description = "Возвращает юзера")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @ApiResponse(responseCode = "404", description = "Номер не найден")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-phone")
    public ResponseEntity<UserDTO> getUserByPhoneNumber(

            @NotBlank(message = "Телефон пользователя, не должен быть пустым")
            @Size(min = 3, max = 20, message = "количество символов 3-20")
            @Pattern(
                    regexp = "^\\d+$",
                    message = "Некорректный номер. Пример: 89065554433"
            )
            @RequestParam("phoneNumber")
            String phoneNumber) {
        return ResponseEntity.ok(userService.findUserDTOByPhoneNumber(phoneNumber));
    }

    /**
     * Удаляет пользователя по фамилии и имени.
     *
     * @param lastName  фамилия (валидируется как русский текст)
     * @param firstName имя (валидируется как русский текст)
     * @return {@link ResponseEntity} со статусом {@code 204 NO CONTENT}
     */
    @Operation(summary = "Удалите Юзер",
            description = "удаляет данные существующего Юзер по фамилии и имени ")
    @ApiResponse(responseCode = "204", description = "Юзер удален")
    @DeleteMapping("/by-name")
    public ResponseEntity<Void> deleteUserByLastAndFirstName(
            @RequestParam("lastName")
            @Size(min = 3, max = 50, message = "количество символов 3-50")
            @Pattern(
                    regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
                    message = "Фамилия может содержать только русские буквы, дефисы"
            )
            @NotBlank(message = "Фамилия пользователя, не должен быть пустым")
            String lastName,
            @RequestParam("firstName")
            @Size(min = 3, max = 50, message = "количество символов 3-50")
            @Pattern(
                    regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
                    message = "Имя может содержать только русские буквы, дефисы"
            )
            @NotBlank(message = "Имя пользователя, не должен быть пустым")
            String firstName) {
        userService.deleteUserByLastNameAndFirstName(lastName, firstName);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param userDTO обновлённые данные пользователя (обязательные и валидные)
     * @return {@link ResponseEntity} с обновлённым {@link UserDTO} и статусом {@code 200 OK}
     */
    @Operation(summary = "обновить юзера ",
            description = "Возвращает обновленного юзера")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @ApiResponse(responseCode = "404", description = "Юзер не найден")
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.ok(userService.changeDataUser(userDTO));
    }
    /**
     * Подтверждает регистрацию пользователя по токену.
     * <p>
     * Возвращает HTML-страницу с результатом подтверждения (успех или ошибка).
     *
     * @param token токен подтверждения, полученный по электронной почте
     * @return {@link ResponseEntity} с HTML-контентом и типом {@code text/html;charset=UTF-8}
     */
    @Operation(summary = "Подтвердить регистрацию юзера ",
            description = "Возвращает обновленного юзера")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @ApiResponse(responseCode = "404", description = "Юзер не найден")
    @GetMapping("/verify")
    public ResponseEntity<String> verifyRegistration(@RequestParam("token") String token) {
        boolean success = userService.confirmRegistrationUser(token);
        String message = success
                ? "<h2>Регистрация подтверждена! Можете войти.</h2>"
                : "<h2>Ошибка: недействительная ссылка.</h2>";

        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>Подтверждение регистрации</title>
        </head>
        <body>
            %s
        </body>
        </html>
        """.formatted(message);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .body(html);
    }
}
