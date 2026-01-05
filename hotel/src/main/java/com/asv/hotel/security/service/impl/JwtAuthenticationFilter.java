package com.asv.hotel.security.service.impl;

import com.asv.hotel.security.util.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Фильтр аутентификации по JWT-токену.
 * <p>
 * Перехватывает входящие HTTP-запросы и проверяет наличие валидного JWT-токена
 * в заголовке {@code Authorization} (формат: {@code Bearer <token>}).
 * </p>
 * <p>
 * Если токен найден, валиден и пользователь существует, создаёт аутентификационный объект
 * и устанавливает его в {@link SecurityContextHolder}, делая пользователя "аутентифицированным"
 * для остальной части обработки запроса.
 * </p>
 * <p>
 * Работает один раз на запрос (наследуется от {@link OncePerRequestFilter}).
 * </p>
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    private final CustomUserDetailsServiceImpl userDetailsService;

    /**
     * Основной метод фильтрации запроса.
     * <p>
     * Логика:
     * <ol>
     *   <li>Извлекает заголовок {@code Authorization};</li>
     *   <li>Проверяет наличие префикса {@code Bearer };</li>
     *   <li>Извлекает nickname из токена;</li>
     *   <li>Если пользователь не аутентифицирован — загружает данные и проверяет валидность токена;</li>
     *   <li>При успехе устанавливает аутентификацию в контекст безопасности.</li>
     * </ol>
     * </p>
     * <p>
     * Если токен отсутствует или недействителен — запрос пропускается без аутентификации
     * (дальнейшая обработка зависит от конфигурации Spring Security).
     * </p>
     *
     * @param request     входящий HTTP-запрос
     * @param response    HTTP-ответ
     * @param filterChain цепочка фильтров для продолжения обработки
     * @throws ServletException если возникает ошибка сервлета
     * @throws IOException      если возникает ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(JWTUtils.AUTHORIZATION);
        final String jwt;
        final String nickName;

        if (authHeader == null || !authHeader.startsWith(JWTUtils.BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(JWTUtils.NUMBER_FOR_CUTTING_TOKEN);

        nickName = jwtUtils.extractUsername(jwt);

        if (nickName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(nickName);

            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
