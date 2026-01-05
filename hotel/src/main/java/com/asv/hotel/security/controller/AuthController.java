package com.asv.hotel.security.controller;

import com.asv.hotel.security.jwt.JWTAuthentication;
import com.asv.hotel.security.jwt.RefreshRequest;
import com.asv.hotel.security.jwt.SignInRequest;
import com.asv.hotel.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Контроллер для управления аутентификацией пользователей через JWT.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>вход в систему с получением пары токенов (access + refresh);</li>
 *   <li>обновление access-токена с использованием refresh-токена;</li>
 *   <li>выход из системы с инвалидацией токенов.</li>
 * </ul>
 * </p>
 * <p>
 * Все эндпоинты защищены от CSRF по умолчанию (в рамках Spring Security + JWT).
 * </p>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "REST API для аутентификации и управления токенами")
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Выполняет аутентификацию пользователя по nickname и паролю.
     * <p>
     * В случае успеха возвращает пару JWT-токенов: краткосрочный access-токен и долгосрочный refresh-токен.
     * </p>
     *
     * @param request DTO с учетными данными ({@code nickName}, {@code password})
     * @return {@link ResponseEntity} с объектом {@link JWTAuthentication}, содержащим токены
     */
    @Operation(
            summary = "Вход в систему",
            description = "Аутентификация пользователя и получение access и refresh токенов"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Неверные учетные данные"
            )
    })
    @PostMapping("/signin")
    public ResponseEntity<JWTAuthentication> signIn(@RequestBody @Valid SignInRequest request) {
        JWTAuthentication response = authenticationService.signIn(request.getNickName(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    /**
     * Обновляет access-токен с использованием валидного refresh-токена.
     * <p>
     * Требуется передать действующий refresh-токен. В ответе возвращается новая пара токенов.
     * </p>
     *
     * @param refreshTokenRequest DTO с refresh-токеном
     * @return {@link ResponseEntity} с новым объектом {@link JWTAuthentication}
     */
    @Operation(
            summary = "Обновление access токена",
            description = "Получение нового access токена по валидному refresh токену")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Токен успешно обновлен"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Некорректный запрос"
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthentication> refreshAccessToken(@RequestBody @Valid RefreshRequest refreshTokenRequest) {
        JWTAuthentication response = authenticationService.refreshAccessToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * Выполняет выход из системы.
     * <p>
     * Удаляет связку токенов (access + refresh) из хранилища (например, Redis или in-memory map),
     * что делает их недействительными до истечения срока жизни.
     * Для идентификации токенов используется информация из HTTP-запроса (например, заголовки).
     * </p>
     *
     * @param request текущий HTTP-запрос, содержащий токены
     * @return {@link ResponseEntity} со статусом {@code 200 OK}
     */
    @Operation(
            summary = "Выход из системы",
            description = "Завершение сессии пользователя и удаление токенов из хранилища")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный выход из системы"),
            @ApiResponse(
                    responseCode = "403",
                    description = "Некорректный запрос")})
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authenticationService.removeTokensFromStorage(request);
        return ResponseEntity.ok().build();
    }
}
