package com.asv.hotel.security.service.impl;

import com.asv.hotel.security.service.TokenStorageService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * In-memory хранилище JWT-токенов для поддержки инвалидации (logout, смена сессии и т.д.).
 * <p>
 * Хранит активные access-токены, сгруппированные по идентификатору пользователя.
 * Каждый токен ассоциирован с флагом активности (в текущей реализации всегда {@code true}).
 * </p>
 * <p>
 * Реализация потокобезопасна за счёт использования {@link ConcurrentHashMap}.
 * Подходит для односерверного развёртывания. Для кластерной среды требуется распределённое хранилище (например, Redis).
 * </p>
 */
@Service
public class TokenStorageServiceImpl implements TokenStorageService {

    private Map<Long, Map<String, Boolean>> userTokens = new ConcurrentHashMap<>();

    /**
     * Структура данных: {@code userId -> { token -> isActive }}.
     * Используется {@link ConcurrentHashMap} для потокобезопасности.
     */
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

    /**
     * Добавляет токен в хранилище для указанного пользователя.
     * <p>
     * Если у пользователя ещё нет токенов — создаётся новая карта.
     * </p>
     *
     * @param token JWT access-токен
     */
    @Override
    public boolean isTokenExpired(String token) {
        return userTokens.values()
                .stream()
                .anyMatch(tokensMap ->
                        tokensMap.containsKey(token) &&
                                Boolean.TRUE.equals(tokensMap.get(token))
                );
    }

    /**
     * Проверяет, существует ли токен в хранилище (т.е. не был ли он отозван).
     * <p>
     * ⚠️ Название метода вводит в заблуждение: на самом деле проверяет,
     * <b>активен ли токен</b>, а не истёк ли его срок.
     * Лучшее имя: {@code isTokenActive}.
     * </p>
     *
     * @param token JWT access-токен
     * @return {@code true}, если токен присутствует в хранилище и активен; иначе {@code false}
     */
    @Override
    public boolean isTokenActiveForUser(Long userId, String token) {
        Map<String, Boolean> userTokenMap = userTokens.get(userId);
        return userTokenMap != null &&
                Boolean.TRUE.equals(userTokenMap.get(token));
    }

    /**
     * Проверяет, принадлежит ли указанный токен пользователю и активен ли он.
     *
     * @param token JWT access-токен
     * @return {@code true}, если токен активен и привязан к пользователю; иначе {@code false}
     */
    @Override
    public void removeToken(String token) {
        userTokens.values()
                .forEach(tokensMap -> tokensMap.remove(token));
    }

    /**
     * Удаляет токен из хранилища у всех пользователей.
     * <p>
     * Используется, например, при logout, если токен передан в заголовке.
     * </p>
     *
     * @param token JWT access-токен
     */
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

    /**
     * Удаляет конкретный токен у указанного пользователя.
     * <p>
     * Если после удаления у пользователя не осталось токенов — удаляется вся запись о пользователе.
     * </p>
     *
     */
    @Override
    public void removeAllUserTokens(Long userId) {
        userTokens.remove(userId);
    }


    /**
     * Возвращает все активные токены указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return множество токенов; пустое множество, если пользователь не найден или у него нет токенов
     */
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


    /**
     * Очищает всё хранилище (все токены всех пользователей).
     * <p>
     * Может использоваться при перезапуске или тестировании.
     * </p>
     */
    @Override
    public void clearAllTokens() {
        userTokens.clear();
    }

}
