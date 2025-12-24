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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "REST API для управления юзерами")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Создать нового юзера",
            description = "создает нового юзера")
    @ApiResponse(responseCode = "201", description = "юзера создан")
    @ApiResponse(responseCode = "409", description = "юзера не создан")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        UserDTO newuserDTO = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newuserDTO);
    }


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


    @Operation(summary = "обновить юзера ",
            description = "Возвращает обновленного юзера")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    @ApiResponse(responseCode = "404", description = "Юзер не найден")
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.ok(userService.changeDataUser(userDTO));
    }

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
