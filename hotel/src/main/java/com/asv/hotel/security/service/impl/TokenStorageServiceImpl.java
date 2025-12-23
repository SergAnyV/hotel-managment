package com.asv.hotel.security.service.impl;

import com.asv.hotel.security.service.TokenStorageService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
public class TokenStorageServiceImpl implements TokenStorageService {

    private Map<Long, Map<String, Boolean>> userTokens = new ConcurrentHashMap<>();

    @Override
    public void addTokens(Long id, String token) {
        userTokens.compute(id, (key, tokensMap) -> {
            if (tokensMap == null) {
                tokensMap = new ConcurrentHashMap<>();
            }
            tokensMap.put(token, Boolean.TRUE);
            return tokensMap;
        });
    }


    @Override
    public boolean isTokenExpired(String token) {
        return userTokens.values()
                .stream()
                .anyMatch(tokensMap ->
                        tokensMap.containsKey(token) &&
                                Boolean.TRUE.equals(tokensMap.get(token))
                );
    }

    @Override
    public boolean isTokenActiveForUser(Long userId, String token) {
        Map<String, Boolean> userTokenMap = userTokens.get(userId);
        return userTokenMap != null &&
                Boolean.TRUE.equals(userTokenMap.get(token));
    }

    @Override
    public void removeToken(String token) {
        userTokens.values()
                .forEach(tokensMap -> tokensMap.remove(token));
    }

    @Override
    public void removeTokenForUser(Long userId, String token) {
        Map<String, Boolean> userTokenMap = userTokens.get(userId);
        if (userTokenMap != null) {
            userTokenMap.remove(token);

            if (userTokenMap.isEmpty()) {
                userTokens.remove(userId);
            }
        }
    }

    @Override
    public void removeAllUserTokens(Long userId) {
        userTokens.remove(userId);
    }

    @Override
    public Set<String> getUserActiveTokens(Long userId) {
        Map<String, Boolean> userTokenMap = userTokens.get(userId);
        if (userTokenMap == null) {
            return Collections.emptySet();
        }
        return userTokenMap.entrySet()
                .stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public void clearAllTokens() {
        userTokens.clear();
    }

}
