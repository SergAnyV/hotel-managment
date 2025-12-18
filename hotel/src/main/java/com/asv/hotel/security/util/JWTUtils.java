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

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTUtils {

    private final JWTSecretsProperties jwtSecretsProperties;
    private final TokenStorageService tokenStorageService;

    private SecretKey cachedAccessKey;
    private SecretKey cachedRefreshKey;

    private static final long EXPIRATION_TIME_FOR_REFRESH_TOKEN = 604_800_000;
    private static final long EXPIRATION_TIME_FOR_ACCESS_TOKEN = 1_800_000;
    private static final long TIME_FOR_CHECKING_TOKEN = 86_400_000;
    private static final MacAlgorithm SIGNATURE_ALGORITHM = Jwts.SIG.HS256;
    private static final String SHIELDED_POINT = "\\.";
    public static final String BEARER = "Bearer";
    public static final String AUTHORIZATION = "Authorization";
    public static final int NUMBER_FOR_CUTTING_TOKEN = 7;


    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, EXPIRATION_TIME_FOR_ACCESS_TOKEN);
    }


    SecretKey getAccessSigningKey() {
        if (cachedAccessKey == null) {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecretsProperties.getAccess());
            cachedAccessKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return cachedAccessKey;
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, EXPIRATION_TIME_FOR_REFRESH_TOKEN);
    }

    SecretKey getRefreshSigningKey() {
        if (cachedRefreshKey == null) {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecretsProperties.getRefresh());
            cachedRefreshKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return cachedRefreshKey;
    }


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

    SecretKey getSigningKeyForType(long expirationTimeMs) {
        if (expirationTimeMs > TIME_FOR_CHECKING_TOKEN) {
            return getRefreshSigningKey();
        } else {
            return getAccessSigningKey();
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token,
                claims -> claims.getSubject()
        );
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

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

    private SecretKey getSigningKeyForToken(String token) {
             String[] parts = token.split(SHIELDED_POINT);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
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

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (userDetails == null) {
            return false;
        }
        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) &&
                !isTokenExpired(token) &&
                tokenStorageService.isTokenExpired(token);
    }


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


    private Date extractExpiration(String token) {
        try {
            return extractClaim(token, Claims::getExpiration);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return e.getClaims().getExpiration();
        }
    }
}
