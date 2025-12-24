package com.asv.hotel.security.jwt;

import com.asv.hotel.security.util.JWTUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

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
@Schema(description = "Ответ с JWT токенами аутентификации")
public class JWTAuthentication {
    /**
     * Access токен для доступа к защищенным ресурсам.
     * Имеет ограниченное время жизни
     * Должен передаваться в заголовке Authorization при запросах к защищенным эндпоинтам.
     */
    @Schema(
            description = "Access токен для доступа к защищенным ресурсам")
    @Size(min = 3, max = 200, message = "Access токен должен содержать от 3 до 200 символов")
    private String accessToken;

    /**
     * Refresh токен для обновления access токена.
     * Имеет более длительное время жизни чем accessToken.
     * Используется для получения нового accessToken без повторной аутентификации.
     */
    @Schema(
            description = "Refresh токен для обновления access токена")
    @Size(min = 3, max = 200, message = "Refresh токен  должен содержать от 3 до 200 символов")
    private String refreshToken;
    /**
     * Тип токена, используемый в заголовке Authorization.
     * По умолчанию имеет значение "Bearer_".
     */
    private static final String TOKEN_TYPE = JWTUtils.BEARER;
}
