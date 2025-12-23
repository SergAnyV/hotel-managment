//package com.asv.hotel.security.service.impl;
//
//import com.asv.hotel.security.util.JWTUtils;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//
//
//import java.io.IOException;
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class JwtAuthenticationFilterTest {
//    @Mock
//    private JWTUtils jwtUtils;
//
//    @Mock
//    private CustomUserDetailsServiceImpl userDetailsService;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private HttpServletResponse response;
//
//    @Mock
//    private FilterChain filterChain;
//
//    @Mock
//    private SecurityContext securityContext;
//
//    @InjectMocks
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    private final String validToken = "valid.jwt.token";
//    private final String bearerToken = "Bearer " + validToken;
//    private final String username = "testuser";
//
//    @BeforeEach
//    void setUp() {
//        SecurityContextHolder.clearContext();
//    }
//
//    @Test
//    void testNoAuthorizationHeader() throws ServletException, IOException {
//        when(request.getHeader("Authorization")).thenReturn(null);
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        verify(filterChain).doFilter(request, response);
//        verifyNoInteractions(jwtUtils, userDetailsService);
//    }
//
//    @Test
//    void testInvalidAuthorizationHeader() throws ServletException, IOException {
//        when(request.getHeader("Authorization")).thenReturn("InvalidPrefix token");
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        verify(filterChain).doFilter(request, response);
//        verifyNoInteractions(jwtUtils, userDetailsService);
//    }
//
//    @Test
//    void testValidTokenAndAuthentication() throws ServletException, IOException {
//        UserDetails userDetails = User.builder()
//                .username(username)
//                .password("password")
//                .authorities(Collections.emptyList())
//                .build();
//
//        when(request.getHeader("Authorization")).thenReturn(bearerToken);
//        when(jwtUtils.extractUsername(validToken)).thenReturn(username);
//        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
//        when(jwtUtils.isTokenValid(validToken, userDetails)).thenReturn(true);
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        verify(jwtUtils).extractUsername(validToken);
//        verify(userDetailsService).loadUserByUsername(username);
//        verify(jwtUtils).isTokenValid(validToken, userDetails);
//        verify(filterChain).doFilter(request, response);
//    }
//
//    @Test
//    void testInvalidToken() throws ServletException, IOException {
//        UserDetails mockUserDetails = mock(UserDetails.class);
//
//        when(request.getHeader("Authorization")).thenReturn(bearerToken);
//        when(jwtUtils.extractUsername(validToken)).thenReturn(username);
//        when(userDetailsService.loadUserByUsername(username)).thenReturn(mockUserDetails);
//
//
//        when(jwtUtils.isTokenValid(eq(validToken), any(UserDetails.class))).thenReturn(false);
//
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        verify(filterChain).doFilter(request, response);
//
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//    }
//
//    @Test
//    void testAlreadyAuthenticated() throws ServletException, IOException {
//        // Set up already authenticated context
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(mock(UsernamePasswordAuthenticationToken.class));
//
//        when(request.getHeader("Authorization")).thenReturn(bearerToken);
//        when(jwtUtils.extractUsername(validToken)).thenReturn(username);
//
//        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
//
//        verify(filterChain).doFilter(request, response);
//        verifyNoInteractions(userDetailsService); // Should not load user details
//    }
//}