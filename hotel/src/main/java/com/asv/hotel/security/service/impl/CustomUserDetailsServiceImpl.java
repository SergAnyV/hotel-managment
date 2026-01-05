package com.asv.hotel.security.service.impl;

import com.asv.hotel.entities.User;
import com.asv.hotel.exceptions.HotelAuthenticationException;
import com.asv.hotel.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * Реализация {@link UserDetailsService} для загрузки пользователей по nickname.
 * <p>
 * Используется Spring Security для аутентификации.
 * Ищет пользователя в репозитории по уникальному полю {@code nickName},
 * которое выступает в роли логина (username).
 * </p>
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    /**
     * Загружает данные пользователя по его уникальному nickname.
     * <p>
     * Если пользователь не найден, выбрасывается {@link HotelAuthenticationException},
     * который на практике приведёт к ошибке аутентификации.
     * </p>
     *
     * @param username nickname пользователя (логин)
     * @return объект {@link User}, реализующий {@link UserDetails}
     * @throws HotelAuthenticationException если пользователь с указанным nickname не существует
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByNickName(username)
                .orElseThrow(() -> new HotelAuthenticationException(String.format("Пользователь с ником '%s' не найден", username)));
        return user;
    }
}
