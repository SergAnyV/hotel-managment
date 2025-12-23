package com.asv.models.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс запроса для аутентификации пользователя в системе.
 * Содержит учетные данные (никнейм и пароль) для входа в систему.
 *
 * <p>Используется в эндпоинте /auth/signin для получения JWT токенов
 * при успешной аутентификации.</p>
 */
@Setter
@Getter
@Builder
public class SignInRequest {
    /**
     * Уникальный никнейм пользователя для идентификации в системе.
     * Должен соответствовать никнейму, указанному при регистрации.
     */
       private String nickName;
    /**
     * Пароль пользователя для аутентификации.
     * Должен соответствовать паролю, установленному при регистрации.
     */
      private String password;
}
