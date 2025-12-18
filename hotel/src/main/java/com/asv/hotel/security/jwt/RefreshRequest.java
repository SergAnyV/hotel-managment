package com.asv.hotel.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * Класс запроса для обновления JWT токенов.
 * Используется для получения нового access токена по валидному refresh токену.
 *
 * <p>Refresh токен должен быть предварительно получен при успешной аутентификации
 * и используется когда access токен истек или стал невалидным.</p>
 */
@Getter
@Schema(description = "Запрос на обновление токена")
public class RefreshRequest {
    /**
     * Refresh токен, полученный при предыдущей успешной аутентификации.
     * Используется для получения новой пары access и refresh токенов
     * без необходимости повторного ввода учетных данных.
     */
    @NotBlank(message = "Refresh token не может быть пустым")
    @Schema(
            description = "Refresh токен для обновления access токена")
    @Size(max = 200, message = "Refresh токен  должен содержать до 200 символов")
    private String refreshToken;
}
