package com.asv.models.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий ответ аутентификации с JWT токенами.
 * Содержит access и refresh токены, а также тип токена.
 * Используется для передачи токенов клиенту после успешной аутентификации
 * или обновления токенов.
 *
 */

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class JWTAuthentication {
    /**
     * Access токен для доступа к защищенным ресурсам.
     * Имеет ограниченное время жизни
     * Должен передаваться в заголовке Authorization при запросах к защищенным эндпоинтам.
     */
    private String accessToken;

    /**
     * Refresh токен для обновления access токена.
     * Имеет более длительное время жизни чем accessToken.
     * Используется для получения нового accessToken без повторной аутентификации.
     */
    private String refreshToken;
    /**
     * Тип токена, используемый в заголовке Authorization.
     * По умолчанию имеет значение "Bearer".
     */
    private static final String TOKEN_TYPE = "Bearer";
}
