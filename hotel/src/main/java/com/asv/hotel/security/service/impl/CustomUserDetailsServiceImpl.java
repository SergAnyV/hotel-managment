package com.asv.hotel.security.service.impl;

import com.asv.hotel.entities.User;
import com.asv.hotel.exceptions.HotelAuthenticationException;
import com.asv.hotel.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByNickName(username)
                .orElseThrow(() -> new HotelAuthenticationException(String.format("Пользователь с ником '%s' не найден", username)));
        return user;
    }
}
