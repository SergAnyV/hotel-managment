package com.asv.hotel.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * Класс запроса для аутентификации пользователя в системе.
 * Содержит учетные данные (никнейм и пароль) для входа в систему.
 *
 * <p>Используется в эндпоинте /auth/signin для получения JWT токенов
 * при успешной аутентификации.</p>
 */
@Setter
@Getter
@Schema(description = "Запрос на аутентификацию пользователя")
public class SignInRequest {
    /**
     * Уникальный никнейм пользователя для идентификации в системе.
     * Должен соответствовать никнейму, указанному при регистрации.
     */
    @NotBlank(message = "Никнейм не может быть пустым")
    @Size(min = 3, max = 30, message = "Никнейм должен содержать от 3 до 30 символов")
    @Schema(
            description = "Уникальный никнейм пользователя",
            example = "cleaner_maria",
            minLength = 3,
            maxLength = 30
    )
    private String nickName;
    /**
     * Пароль пользователя для аутентификации.
     * Должен соответствовать паролю, установленному при регистрации.
     */
    @NotBlank(message = "Пароль не может быть пустым")
    @Size( max = 120, message = "Пароль должен содержать до 120 символов")
    @Schema(
            description = "Пароль пользователя",
            example = "password",
            maxLength = 20
    )
    private String password;
}
