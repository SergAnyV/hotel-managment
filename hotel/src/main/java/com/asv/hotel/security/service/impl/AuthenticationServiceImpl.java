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

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsServiceImpl userDetailsService;

    private final JWTUtils jwtUtils;

    private final TokenStorageService tokenStorageService;

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
