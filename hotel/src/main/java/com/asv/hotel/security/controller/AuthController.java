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

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "REST API для аутентификации и управления токенами")
public class AuthController {

    private final AuthenticationService authenticationService;

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
