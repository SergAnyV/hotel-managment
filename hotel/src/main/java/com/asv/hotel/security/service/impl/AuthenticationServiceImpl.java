package com.asv.hotel.security.service.impl;

import com.asv.hotel.entities.User;
import com.asv.hotel.exceptions.HotelAuthenticationException;
import com.asv.hotel.security.jwt.JWTAuthentication;
import com.asv.hotel.security.service.AuthenticationService;
import com.asv.hotel.security.service.TokenStorageService;
import com.asv.hotel.security.util.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса аутентификации с использованием JWT.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>вход пользователя по логину и паролю с генерацией пары токенов (access + refresh);</li>
 *   <li>обновление access-токена по валидному refresh-токену;</li>
 *   <li>удаление токенов из хранилища при выходе из системы.</li>
 * </ul>
 * </p>
 * <p>
 * Управляет контекстом безопасности Spring и взаимодействует с внешним хранилищем токенов
 * для поддержки инвалидации (например, при logout).
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsServiceImpl userDetailsService;

    private final JWTUtils jwtUtils;

    private final TokenStorageService tokenStorageService;

    /**
     * Выполняет аутентификацию пользователя и генерирует JWT-токены.
     * <p>
     * После успешной аутентификации:
     * <ul>
     *   <li>устанавливает аутентификацию в {@link SecurityContextHolder};</li>
     *   <li>генерирует access и refresh токены;</li>
     *   <li>инвалидирует предыдущие токены пользователя и сохраняет новый access-токен в хранилище.</li>
     * </ul>
     * </p>
     *
     * @param logIn    логин пользователя (обычно nickname)
     * @param password пароль в открытом виде
     * @return объект {@link JWTAuthentication} с парой токенов
     * @throws HotelAuthenticationException если аутентификация не удалась
     */
    @Override
    public JWTAuthentication signIn(String logIn, String password) {

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(logIn, password);

        final Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final User userDetails = (User) authentication.getPrincipal();

        final String accessToken = jwtUtils.generateAccessToken(userDetails);
        final String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        final Long id = userDetails.getId();

        tokenStorageService.removeAllUserTokens(id);
        tokenStorageService.addTokens(id, accessToken);

        return new JWTAuthentication(accessToken, refreshToken);
    }

    /**
     * Обновляет access-токен с использованием refresh-токена.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>валидность refresh-токена;</li>
     *   <li>принадлежность токена пользователю (подпись и данные совпадают).</li>
     * </ul>
     * При успехе генерирует новый access-токен и сохраняет его в хранилище.
     * </p>
     *
     * @param refreshToken валидный refresh-токен
     * @return объект {@link JWTAuthentication} с новым access-токеном и исходным refresh-токеном
     * @throws HotelAuthenticationException если токен недействителен или не принадлежит пользователю
     */
    @Override
    public JWTAuthentication refreshAccessToken(String refreshToken) {
        try {

            if (!jwtUtils.isTokenValid(refreshToken, null)) {
                throw new HotelAuthenticationException("Invalid refresh token");
            }

            String nickName = jwtUtils.extractUsername(refreshToken);

            User userDetails = (User) userDetailsService.loadUserByUsername(nickName);
            Long id=userDetails.getId();

            if (!jwtUtils.isTokenValid(refreshToken, userDetails)) {
                throw new HotelAuthenticationException("Refresh token does not belong to the user");
            }

            String newAccessToken = jwtUtils.generateAccessToken(userDetails);

            tokenStorageService.addTokens(id,newAccessToken);

            return new JWTAuthentication(newAccessToken, refreshToken);

        } catch (HotelAuthenticationException e) {
        log.error("Error: проблемы с обновлением токена через refreshToken {}",refreshToken);
            throw e;
        } catch (Exception e) {
            log.error("Error: проблемы с обновлением токена через refreshToken {} , не связанные с аутонтефикацией ",refreshToken ,e);
            throw new HotelAuthenticationException(String.format( "Failed to refresh access token: ''%s ",e.getMessage()));
        }
    }

    /**
     * Удаляет access-токен из хранилища при выходе пользователя.
     * <p>
     * Извлекает токен из заголовка {@code Authorization} запроса (формат: {@code Bearer <token>}).
     * Если заголовок отсутствует или имеет неверный формат — операция игнорируется.
     * </p>
     *
     * @param request HTTP-запрос, содержащий access-токен в заголовке
     */
    @Override
    public void removeTokensFromStorage(HttpServletRequest request) {
        // токен из заголовка
        String authHeader = request.getHeader(JWTUtils.AUTHORIZATION);
        String accessToken = null;

        if (authHeader != null && authHeader.startsWith(JWTUtils.BEARER)) {
            accessToken = authHeader.substring(7);
        }
        tokenStorageService.removeToken(accessToken);
    }


}
