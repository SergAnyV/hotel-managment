//package com.asv.hotel.security.service.impl;
//
//import com.asv.hotel.entities.User;
//import com.asv.hotel.exceptions.HotelAuthenticationException;
//import com.asv.hotel.security.jwt.JWTAuthentication;
//import com.asv.hotel.security.service.TokenStorageService;
//import com.asv.hotel.security.util.JWTUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthenticationServiceImplTest {
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private CustomUserDetailsServiceImpl userDetailsService;
//
//    @Mock
//    private JWTUtils jwtUtils;
//
//    @Mock
//    private TokenStorageService tokenStorageService;
//
//    @InjectMocks
//    private AuthenticationServiceImpl authenticationService;
//
//    private final String TEST_NICKNAME = "testUser";
//    private final String TEST_PASSWORD = "password123";
//    private final String TEST_ACCESS_TOKEN = "accessToken123";
//    private final String TEST_REFRESH_TOKEN = "refreshToken456";
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        testUser = new User();
//        testUser.setNickName(TEST_NICKNAME);
//        testUser.setPassword(TEST_PASSWORD);
//
//        // Очищаем SecurityContext перед каждым тестом
//        SecurityContextHolder.clearContext();
//    }
//
//    @Test
//    @DisplayName("signIn: должен успешно аутентифицировать и вернуть токены")
//    void signIn_ShouldAuthenticateAndReturnTokens() {
//
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getPrincipal()).thenReturn(testUser);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(authentication);
//
//        when(jwtUtils.generateAccessToken(testUser)).thenReturn(TEST_ACCESS_TOKEN);
//        when(jwtUtils.generateRefreshToken(testUser)).thenReturn(TEST_REFRESH_TOKEN);
//
//
//        JWTAuthentication response = authenticationService.signIn(TEST_NICKNAME, TEST_PASSWORD);
//
//
//        assertNotNull(response);
//        assertEquals(TEST_ACCESS_TOKEN, response.getAccessToken());
//        assertEquals(TEST_REFRESH_TOKEN, response.getRefreshToken());
//
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtUtils).generateAccessToken(testUser);
//        verify(jwtUtils).generateRefreshToken(testUser);
//
//        // Проверяем, что аутентификация установлена в контекст
//        Authentication contextAuth = SecurityContextHolder.getContext().getAuthentication();
//        assertEquals(authentication, contextAuth);
//    }
//
//    @Test
//    @DisplayName("signIn: должен бросить исключение при неудачной аутентификации")
//    void signIn_ShouldThrowExceptionWhenAuthenticationFails() {
//        // Arrange
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new BadCredentialsException("Invalid credentials"));
//
//        // Act & Assert
//        assertThrows(BadCredentialsException.class, () -> {
//            authenticationService.signIn(TEST_NICKNAME, TEST_PASSWORD);
//        });
//
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verifyNoInteractions(jwtUtils, tokenStorageService);
//    }
//
//    @Test
//    @DisplayName("refreshAccessToken: должен успешно обновить access token")
//    void refreshAccessToken_ShouldRefreshAccessTokenSuccessfully() {
//        String newAccessToken = "newAccessToken789";
//
//        when(jwtUtils.isTokenValid(TEST_REFRESH_TOKEN, null)).thenReturn(true);
//        when(jwtUtils.extractUsername(TEST_REFRESH_TOKEN)).thenReturn(TEST_NICKNAME);
//        when(userDetailsService.loadUserByUsername(TEST_NICKNAME)).thenReturn(testUser);
//        when(jwtUtils.isTokenValid(TEST_REFRESH_TOKEN, testUser)).thenReturn(true);
//        when(jwtUtils.generateAccessToken(testUser)).thenReturn(newAccessToken);
//
//        JWTAuthentication response = authenticationService.refreshAccessToken(TEST_REFRESH_TOKEN);
//
//        assertNotNull(response);
//        assertEquals(newAccessToken, response.getAccessToken());
//        assertEquals(TEST_REFRESH_TOKEN, response.getRefreshToken());
//
//        verify(jwtUtils).isTokenValid(TEST_REFRESH_TOKEN, null);
//        verify(jwtUtils).extractUsername(TEST_REFRESH_TOKEN);
//        verify(userDetailsService).loadUserByUsername(TEST_NICKNAME);
//        verify(jwtUtils).isTokenValid(TEST_REFRESH_TOKEN, testUser);
//        verify(jwtUtils).generateAccessToken(testUser);
//
//    }
//
//    @Test
//    @DisplayName("refreshAccessToken: должен бросить исключение при невалидном refresh token")
//    void refreshAccessToken_ShouldThrowExceptionForInvalidRefreshToken() {
//
//        when(jwtUtils.isTokenValid(TEST_REFRESH_TOKEN, null)).thenReturn(false);
//
//
//        HotelAuthenticationException exception = assertThrows(HotelAuthenticationException.class, () -> {
//            authenticationService.refreshAccessToken(TEST_REFRESH_TOKEN);
//        });
//
//        assertEquals("Invalid refresh token", exception.getMessage());
//        verify(jwtUtils).isTokenValid(TEST_REFRESH_TOKEN, null);
//        verifyNoMoreInteractions(jwtUtils);
//        verifyNoInteractions(userDetailsService, tokenStorageService);
//    }
//
//    @Test
//    @DisplayName("refreshAccessToken: должен бросить исключение когда refresh token не принадлежит пользователю")
//    void refreshAccessToken_ShouldThrowExceptionWhenTokenDoesNotBelongToUser() {
//        // Arrange
//        when(jwtUtils.isTokenValid(TEST_REFRESH_TOKEN, null)).thenReturn(true);
//        when(jwtUtils.extractUsername(TEST_REFRESH_TOKEN)).thenReturn(TEST_NICKNAME);
//        when(userDetailsService.loadUserByUsername(TEST_NICKNAME)).thenReturn(testUser);
//        when(jwtUtils.isTokenValid(TEST_REFRESH_TOKEN, testUser)).thenReturn(false);
//
//
//        HotelAuthenticationException exception = assertThrows(HotelAuthenticationException.class, () -> {
//            authenticationService.refreshAccessToken(TEST_REFRESH_TOKEN);
//        });
//
//        assertTrue(exception.getMessage().contains("Refresh token does not belong to the user"));
//
//
//        verify(jwtUtils).isTokenValid(TEST_REFRESH_TOKEN, null);
//        verify(jwtUtils).extractUsername(TEST_REFRESH_TOKEN);
//        verify(userDetailsService).loadUserByUsername(TEST_NICKNAME);
//        verify(jwtUtils).isTokenValid(TEST_REFRESH_TOKEN, testUser);
//        verifyNoMoreInteractions(jwtUtils);
//        verifyNoInteractions(tokenStorageService);
//    }
//
//    @Test
//    @DisplayName("refreshAccessToken: должен бросить исключение при ошибке загрузки пользователя")
//    void refreshAccessToken_ShouldThrowExceptionWhenUserLoadingFails() {
//
//        when(jwtUtils.isTokenValid(TEST_REFRESH_TOKEN, null)).thenReturn(true);
//        when(jwtUtils.extractUsername(TEST_REFRESH_TOKEN)).thenReturn(TEST_NICKNAME);
//        when(userDetailsService.loadUserByUsername(TEST_NICKNAME))
//                .thenThrow(new RuntimeException("User not found"));
//
//
//        HotelAuthenticationException exception = assertThrows(HotelAuthenticationException.class, () -> {
//            authenticationService.refreshAccessToken(TEST_REFRESH_TOKEN);
//        });
//
//        assertTrue(exception.getMessage().contains("Failed to refresh access token"));
//        assertTrue(exception.getMessage().contains("User not found"));
//
//        verify(jwtUtils).isTokenValid(TEST_REFRESH_TOKEN, null);
//        verify(jwtUtils).extractUsername(TEST_REFRESH_TOKEN);
//        verify(userDetailsService).loadUserByUsername(TEST_NICKNAME);
//        verifyNoMoreInteractions(jwtUtils);
//        verifyNoInteractions(tokenStorageService);
//    }
//
//    @Test
//    @DisplayName("refreshAccessToken: должен обработать общее исключение")
//    void refreshAccessToken_ShouldHandleGeneralException() {
//        // Arrange
//        when(jwtUtils.isTokenValid(TEST_REFRESH_TOKEN, null))
//                .thenThrow(new RuntimeException("Unexpected error"));
//
//        // Act & Assert
//        HotelAuthenticationException exception = assertThrows(HotelAuthenticationException.class, () -> {
//            authenticationService.refreshAccessToken(TEST_REFRESH_TOKEN);
//        });
//
//        assertTrue(exception.getMessage().contains("Failed to refresh access token"));
//        assertTrue(exception.getMessage().contains("Unexpected error"));
//
//        verify(jwtUtils).isTokenValid(TEST_REFRESH_TOKEN, null);
//        verifyNoMoreInteractions(jwtUtils);
//        verifyNoInteractions(userDetailsService, tokenStorageService);
//    }
//
//}