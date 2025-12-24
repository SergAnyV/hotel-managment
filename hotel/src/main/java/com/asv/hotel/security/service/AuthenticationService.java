package com.asv.hotel.security.service;

import com.asv.hotel.security.jwt.JWTAuthentication;
import jakarta.servlet.http.HttpServletRequest;
/**
 * Интерфейс сервиса для управления аутентификацией пользователей и токенами.
 * Предоставляет функциональность для входа в систему, обновления токенов и их удаления.
 *
 * <p>Данный сервис отвечает за:
 * <ul>
 *   <li>Аутентификацию пользователей по учетным данным</li>
 *   <li>Генерацию JWT access и refresh токенов</li>
 *   <li>Обновление истекших access токенов с использованием refresh токенов</li>
 *   <li>Управление хранением и аннулированием токенов</li>
 * </ul>
 *
 */
public interface AuthenticationService {
    /**
     * Аутентифицирует пользователя с предоставленными учетными данными и генерирует JWT токены.
     *
     * <p>Этот метод:
     * <ul>
     *   <li>Проверяет учетные данные пользователя через authentication manager</li>
     *   <li>Генерирует новые JWT access токен и refresh токен</li>
     *   <li>Сохраняет токены в сервисе хранения токенов</li>
     * </ul>
     *
     * @param nickName имя пользователя или логин для идентификации
     * @param password пароль пользователя для аутентификации
     * @return {@link JWTAuthentication} содержащий сгенерированные access и refresh токены
     */
    JWTAuthentication signIn(String nickName, String password);
    /**
     * Обновляет истекший access токен с использованием валидного refresh токена.Генерирует новый access токен для пользователя
     * @param refreshToken валидный refresh токен для получения нового access токена
     * @return {@link JWTAuthentication} содержащий новый access токен и оригинальный refresh токен
     */
    JWTAuthentication refreshAccessToken(String refreshToken);

    /**
     * Удаляет токены аутентификации из хранилища на основе текущего запроса.
     * Он извлекает access токен из заголовка Authorization и удаляет его
     * из базы хранения токенов.
     * @param request HTTP servlet запрос содержащий заголовок Authorization
     */
    void removeTokensFromStorage(HttpServletRequest request);
}
