package com.asv.hotel.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Класс для хранения секретных ключей JWT.
 * Содержит отдельные ключи для подписи access и refresh токенов.
 *
 * <p>Ключи загружаются из файла application.properties
 * с префиксом {@code jwt.secret}.
 */

@Component
@Getter
@ConfigurationProperties(prefix = "jwt.secret")
@Setter
public class JWTSecretsProperties {
    /**
     * Секретный ключ для подписи access токенов.
     *
     * <p>Требования к ключу:</p>
     * <ul>
     *   <li>Минимальная длина: 32 символа</li>
     *   <li>Не должен использоваться для других целей</li>
     * </ul>
     */
    private String access;

    /**
     * Секретный ключ для подписи refresh токенов.
     * Refresh токены имеют длительное время жизни чем access и используются для обновления access токенов.
     *
     * <p>Требования к ключу:</p>
     * <ul>
     *   <li>Должен отличаться от access ключа</li>
     *   <li>Минимальная длина: 32 символа</li>
     * </ul>
     */
    private String refresh;

}
