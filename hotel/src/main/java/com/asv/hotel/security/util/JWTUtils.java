package com.asv.hotel.security.util;

import com.asv.hotel.configurations.JWTSecretsProperties;
import com.asv.hotel.security.service.TokenStorageService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
/**
 * Утилитный класс для работы с JWT (JSON Web Tokens).
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>генерацию access и refresh токенов с разными ключами и сроками жизни;</li>
 *   <li>извлечение данных из токена (username, дата истечения и др.);</li>
 *   <li>проверку валидности токена (подпись, срок действия, принадлежность пользователю, активность в хранилище).</li>
 * </ul>
 * </p>
 * <p>
 * Использует разные секретные ключи для access и refresh токенов.
 * Определяет тип токена по его сроку жизни:
 * <ul>
 *   <li>access-токен: 30 минут ({@value #EXPIRATION_TIME_FOR_ACCESS_TOKEN} мс);</li>
 *   <li>refresh-токен: 7 дней ({@value #EXPIRATION_TIME_FOR_REFRESH_TOKEN} мс).</li>
 * </ul>
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JWTUtils {

    private final JWTSecretsProperties jwtSecretsProperties;
    private final TokenStorageService tokenStorageService;

    private SecretKey cachedAccessKey;
    private SecretKey cachedRefreshKey;
    /** Срок жизни refresh-токена: 7 дней в миллисекундах */ /** Срок жизни refresh-токена: 7 дней в миллисекундах */
    private static final long EXPIRATION_TIME_FOR_REFRESH_TOKEN = 604_800_000;
    /** Срок жизни access-токена: 30 минут в миллисекундах */
    private static final long EXPIRATION_TIME_FOR_ACCESS_TOKEN = 1_800_000;
    /** Порог для различения типов токенов: 1 день в миллисекундах */
    private static final long TIME_FOR_CHECKING_TOKEN = 86_400_000;
    /** Алгоритм подписи: HMAC-SHA256 */
    private static final MacAlgorithm SIGNATURE_ALGORITHM = Jwts.SIG.HS256;
    /** Экранированная точка для разделения частей токена */
    private static final String SHIELDED_POINT = "\\.";
    /** Префикс для заголовка авторизации */
    public static final String BEARER = "Bearer ";
    /** Имя заголовка авторизации */
    public static final String AUTHORIZATION = "Authorization";
    /** Количество символов для обрезки "Bearer " */
    public static final int NUMBER_FOR_CUTTING_TOKEN = 7;

    /**
     * Генерирует access-токен для пользователя.
     *
     * @param userDetails данные пользователя
     * @return подписанная JWT-строка
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, EXPIRATION_TIME_FOR_ACCESS_TOKEN);
    }

    /**
     * Возвращает ключ для подписи access-токенов (кэшируется после первого вызова).
     *
     * @return секретный ключ
     */
    SecretKey getAccessSigningKey() {
        if (cachedAccessKey == null) {
            String accessSecret = jwtSecretsProperties.getAccess();

            byte[] keyBytes = Decoders.BASE64.decode(accessSecret);
            cachedAccessKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return cachedAccessKey;
    }

    /**
     * Генерирует refresh-токен для пользователя.
     *
     * @param userDetails данные пользователя
     * @return подписанная JWT-строка
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, EXPIRATION_TIME_FOR_REFRESH_TOKEN);
    }

    /**
     * Возвращает ключ для подписи refresh-токенов (кэшируется после первого вызова).
     *
     * @return секретный ключ
     */
    SecretKey getRefreshSigningKey() {
        if (cachedRefreshKey == null) {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecretsProperties.getRefresh());
            cachedRefreshKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return cachedRefreshKey;
    }

    /**
     * Общий метод генерации JWT.
     *
     * @param extraClaims дополнительные claims
     * @param userDetails данные пользователя
     * @param expirationTimeMs срок жизни токена в миллисекундах
     * @return подписанная JWT-строка
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTimeMs
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(
                        userDetails.getUsername())
                .issuedAt(
                        new Date(System.currentTimeMillis()))
                .expiration(
                        new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(
                        getSigningKeyForType(expirationTimeMs)
                        , SIGNATURE_ALGORITHM)
                .compact();
    }

    /**
     * Выбирает ключ подписи в зависимости от срока жизни токена.
     *
     * @param expirationTimeMs срок жизни токена в миллисекундах
     * @return ключ для access или refresh
     */
    SecretKey getSigningKeyForType(long expirationTimeMs) {
        if (expirationTimeMs > TIME_FOR_CHECKING_TOKEN) {
            return getRefreshSigningKey();
        } else {
            return getAccessSigningKey();
        }
    }

    /**
     * Извлекает username (обычно nickname) из токена.
     *
     * @param token JWT-строка
     * @return имя пользователя
     */
    public String extractUsername(String token) {
        return extractClaim(token,
                claims -> claims.getSubject()
        );
    }

    /**
     * Извлекает произвольный claim из токена.
     *
     * @param token          JWT-строка
     * @param claimsResolver функция извлечения значения из claims
     * @param <T>            тип возвращаемого значения
     * @return извлечённое значение
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все claims из токена, даже если он просрочен.
     *
     * @param token JWT-строка
     * @return объект claims
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSigningKeyForToken(token))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
                return e.getClaims();
        }
    }

    /**
     * Определяет тип токена (access/refresh) по его payload и выбирает соответствующий ключ.
     * <p>
     * Анализирует поля {@code iat} (issued at) и {@code exp} (expiration) в декодированном payload.
     * </p>
     *
     * @param token JWT-строка
     * @return ключ для проверки подписи
     */
    private SecretKey getSigningKeyForToken(String token) {
             String[] parts = token.split(SHIELDED_POINT);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        String payload = new String(Decoders.BASE64URL.decode(parts[1]));
        JsonObject jsonPayload = JsonParser.parseString(payload).getAsJsonObject();

        long exp = jsonPayload.get("exp").getAsLong() * 1000;
        long iat = jsonPayload.get("iat").getAsLong() * 1000;
        long tokenLifetime = exp - iat;

        if (tokenLifetime > TIME_FOR_CHECKING_TOKEN) {
            return getRefreshSigningKey();
        } else {
            return getAccessSigningKey();
        }
    }

    /**
     * Проверяет валидность токена.
     * <p>
     * Условия валидности:
     * <ul>
     *   <li>username в токене совпадает с username пользователя;</li>
     *   <li>токен не просрочен;</li>
     *   <li>токен присутствует в хранилище (не был отозван).</li>
     * </ul>
     * </p>
     *
     * @param token       JWT-строка
     * @param userDetails данные пользователя (может быть null)
     * @return {@code true}, если токен валиден; иначе {@code false}
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (userDetails == null) {
            return false;
        }
        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) &&
                !isTokenExpired(token) &&
                tokenStorageService.isTokenExpired(token);
    }

    /**
     * Проверяет, истёк ли срок действия токена.
     * <p>
     * Если токен просрочен — автоматически удаляется из хранилища.
     * </p>
     *
     * @param token JWT-строка
     * @return {@code true}, если токен просрочен; иначе {@code false}
     */
    boolean isTokenExpired(String token) {
        try {
            if (extractExpiration(token).before(new Date())) {
                tokenStorageService.removeToken(token);
                return true;
            }

            return false;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Извлекает дату истечения из токена (даже если он просрочен).
     *
     * @param token JWT-строка
     * @return дата истечения
     */
    private Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return e.getClaims().getExpiration();
        }
    }
}
