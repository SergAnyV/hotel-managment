//package com.asv.hotel.security.util;
//
//
//import com.asv.hotel.configurations.JWTSecretsProperties;
//import com.asv.hotel.security.service.TokenStorageService;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.MacAlgorithm;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class JWTUtilsTest {
//    // Моки зависимостей
//    private JWTSecretsProperties jwtSecretsProperties;
//    private TokenStorageService tokenStorageService;
//
//    // Тестируемый объект
//    private JWTUtils jwtUtils;
//
//    // Константы для тестов
//    private static final String TEST_ACCESS_SECRET = "dGVzdF9hY2Nlc3Nfc2VjcmV0X2tleV9mb3JfdG9rZW5zXzMyX2NoYXJzX21pbg=="; // "test_access_secret_key_for_tokens_32_chars_min" в base64
//    private static final String TEST_REFRESH_SECRET = "dGVzdF9yZWZyZXNoX3NlY3JldF9rZXlfb25seV9mb3JfcmVmcmVzaF90b2tlbnM="; // "test_refresh_secret_key_only_for_refresh_tokens" в base64
//    private static final String TEST_USERNAME = "BigBro";
//    private static final String TEST_ROLE = "администратор";
//    private static final MacAlgorithm SIGNATURE_ALGORITHM = Jwts.SIG.HS256;
//    private static final long EXPIRATION_TIME_FOR_ACCESS_TOKEN = 1_800_000;
//    private static final long EXPIRATION_TIME_FOR_REFRESH_TOKEN = 604_800_000;
//
//    @BeforeEach
//    void setUp() {
//        // Создаем моки
//        jwtSecretsProperties = mock(JWTSecretsProperties.class);
//        tokenStorageService = mock(TokenStorageService.class);
//
//
//        // Настраиваем моки
//        when(jwtSecretsProperties.getAccess()).thenReturn(TEST_ACCESS_SECRET);
//        when(jwtSecretsProperties.getRefresh()).thenReturn(TEST_REFRESH_SECRET);
//        jwtUtils = new JWTUtils(jwtSecretsProperties, tokenStorageService);
//
//
//    }
//
//    @Test
//    @DisplayName("generateAccessToken: должен генерировать валидный Access Token с правильным сроком жизни")
//    void generateAccessToken_ShouldGenerateValidTokenWithCorrectExpiration() {
//        // Arrange
//        UserDetails userDetails = createUserDetails(TEST_USERNAME, TEST_ROLE);
//
//        // Act
//        String accessToken = jwtUtils.generateAccessToken(userDetails);
//
//        // Assert
//        assertNotNull(accessToken, "Access Token не должен быть null");
//        assertFalse(accessToken.isBlank(), "Access Token не должен быть пустым");
//
//        // Проверяем, что токен можно распарсить и извлечь данные
//        String extractedUsername = jwtUtils.extractUsername(accessToken);
//        assertEquals(TEST_USERNAME, extractedUsername, "Извлеченный username должен совпадать с исходным");
//
//        // Проверяем срок жизни (должен быть около 30 минут)
//        Date expirationDate = jwtUtils.extractClaim(accessToken, Claims::getExpiration);
//        Date now = new Date();
//        assertTrue(expirationDate.after(now), "Токен должен быть еще действителен");
//        long timeDiff = expirationDate.getTime() - now.getTime();
//        assertTrue(timeDiff > 1_700_000 && timeDiff < 1_900_000,
//                "Срок жизни Access Token должен быть примерно 30 минут (1 800 000 мс)");
//    }
//
//    @Test
//    @DisplayName("generateRefreshToken: должен генерировать валидный Refresh Token с правильным сроком жизни")
//    void generateRefreshToken_ShouldGenerateValidTokenWithCorrectExpiration() {
//        //создание для секьюрити объекта
//        UserDetails userDetails = createUserDetails(TEST_USERNAME, TEST_ROLE);
//
//        //создаем токен на снове объекта из секьюрити
//        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
//
//
//        assertNotNull(refreshToken, "Refresh Token не должен быть null");
//        assertFalse(refreshToken.isBlank(), "Refresh Token не должен быть пустым");
//
//        // проверяем, что токен можно распарсить и извлечь данные
//        String extractedUsername = jwtUtils.extractUsername(refreshToken);
//        assertEquals(TEST_USERNAME, extractedUsername, "Извлеченный username должен совпадать с исходным");
//
//        // проверяем срок жизни (должен быть около 7 дней)
//        Date expirationDate = jwtUtils.extractClaim(refreshToken, Claims::getExpiration);
//        Date now = new Date();
//        assertTrue(expirationDate.after(now), "Токен должен быть еще действителен");
//        long timeDiff = expirationDate.getTime() - now.getTime();
//        assertTrue(timeDiff > 600_000_000 && timeDiff < 610_000_000,
//                "Срок жизни Refresh Token должен быть примерно 7 дней (604 800 000 мс)");
//    }
//
//    @Test
//    @DisplayName("extractUsername: должен корректно извлекать username из токена")
//    void extractUsername_ShouldExtractUsernameCorrectly() {
//
//        UserDetails userDetails = createUserDetails(TEST_USERNAME, TEST_ROLE);
//        String token = jwtUtils.generateAccessToken(userDetails);
//
//
//        String extractedUsername = jwtUtils.extractUsername(token);
//
//
//        assertEquals(TEST_USERNAME, extractedUsername, "Извлеченный username должен совпадать с исходным");
//    }
//
//    @Test
//    @DisplayName("extractClaim: должен корректно извлекать любое утверждение из токена")
//    void extractClaim_ShouldExtractAnyClaimCorrectly() {
//
//        UserDetails userDetails = createUserDetails(TEST_USERNAME, TEST_ROLE);
//        String token = jwtUtils.generateAccessToken(userDetails);
//
//
//        Date issuedAt = jwtUtils.extractClaim(token, Claims::getIssuedAt);
//        Date expiration = jwtUtils.extractClaim(token, Claims::getExpiration);
//
//
//        assertNotNull(issuedAt, "Дата выдачи не должна быть null");
//        assertNotNull(expiration, "Дата истечения не должна быть null");
//        assertTrue(expiration.after(issuedAt), "Дата истечения должна быть позже даты выдачи");
//    }
//
//
//    @Test
//    @DisplayName("isTokenValid: должен возвращать false, если токен отозван (не активен)")
//    void isTokenValid_ShouldReturnFalseForRevokedToken() {
//
//        UserDetails userDetails = createUserDetails(TEST_USERNAME, TEST_ROLE);
//        String token = jwtUtils.generateAccessToken(userDetails);
//
//        //  токен НЕ активен
//        when(tokenStorageService.isTokenExpired(token)).thenReturn(false);
//
//        boolean isValid = jwtUtils.isTokenValid(token, userDetails);
//
//        assertFalse(isValid, "Отозванный токен должен быть невалиден");
//    }
//
//    @Test
//    @DisplayName("isTokenValid: должен возвращать false, если username не совпадает")
//    void isTokenValid_ShouldReturnFalseForWrongUsername() {
//
//        UserDetails userDetails = createUserDetails(TEST_USERNAME, TEST_ROLE);
//        String token = jwtUtils.generateAccessToken(userDetails);
//
//        // пользователя с другим username
//        UserDetails wrongUser = createUserDetails("WrongUser", TEST_ROLE);
//
//        //  токен активен
//        when(tokenStorageService.isTokenExpired(token)).thenReturn(true);
//
//        boolean isValid = jwtUtils.isTokenValid(token, wrongUser);
//
//
//        assertFalse(isValid, "Токен должен быть невалиден, если username не совпадает");
//    }
//
//    @Test
//    @DisplayName("isTokenExpired: должен корректно определять просроченный токен")
//    void isTokenExpired_ShouldCorrectlyIdentifyExpiredToken() {
//
//        String expiredToken = Jwts.builder()
//                .subject(TEST_USERNAME)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + 1)) // 1 миллисекунда
//                .signWith(jwtUtils.getAccessSigningKey(), SIGNATURE_ALGORITHM)
//                .compact();
//        try {
//            Thread.currentThread().sleep(2);
//        } catch (InterruptedException e) {
//        }
//        when(tokenStorageService.isTokenExpired(expiredToken)).thenReturn(true);
//
//        boolean isExpired = jwtUtils.isTokenExpired(expiredToken);
//
//
//        assertTrue(isExpired, "Токен должен быть просрочен");
//    }
//
//    @Test
//    @DisplayName("isTokenExpired: должен корректно определять действительный токен")
//    void isTokenExpired_ShouldCorrectlyIdentifyValidToken() {
//
//        UserDetails userDetails = createUserDetails(TEST_USERNAME, TEST_ROLE);
//        String validToken = jwtUtils.generateAccessToken(userDetails);
//
//        boolean isExpired = jwtUtils.isTokenExpired(validToken);
//
//        assertFalse(isExpired, "Токен должен быть действительным");
//    }
//
//    @Test
//    @DisplayName("getSigningKeyForType: должен возвращать Access ключ для короткого срока жизни")
//    void getSigningKeyForType_ShouldReturnAccessKeyForShortExpiration() {
//
//        SecretKey key = jwtUtils.getSigningKeyForType(EXPIRATION_TIME_FOR_ACCESS_TOKEN);
//
//
//        assertEquals(jwtUtils.getAccessSigningKey(), key, "Должен возвращаться Access ключ");
//    }
//
//    @Test
//    @DisplayName("getSigningKeyForType: должен возвращать Refresh ключ для длинного срока жизни")
//    void getSigningKeyForType_ShouldReturnRefreshKeyForLongExpiration() {
//        SecretKey key = jwtUtils.getSigningKeyForType(EXPIRATION_TIME_FOR_REFRESH_TOKEN);
//        assertEquals(jwtUtils.getRefreshSigningKey(), key, "Должен возвращаться Refresh ключ");
//    }
//
//    // Вспомогательный метод для создания UserDetails
//    private UserDetails createUserDetails(String username, String role) {
//        //здесь юзер из секьюрити а не мой кастомный
//        return User.builder()
//                .username(username)
//                .password("password") // Пароль не используется в JWT, но нужен для создания объекта
//                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + role)))
//                .build();
//    }
//}