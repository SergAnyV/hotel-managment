package com.asv.hotel.exceptions;


import com.asv.hotel.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
/**
 * Исключение, возникающее при ошибках аутентификации пользователя в системе отеля.
 * <p>
 * Выбрасывается, когда предоставленные учётные данные недействительны, отсутствуют
 * или истёк срок действия сессии/токена (например, неверный логин/пароль, отсутствие JWT,
 * просроченный токен и т.д.).
 * </p>
 * <p>
 * Сопоставляется с HTTP-статусом {@link HttpStatus#UNAUTHORIZED} (401),
 * что указывает на необходимость повторной аутентификации.
 * </p>
 */
public class HotelAuthenticationException extends HotelMainException {
        /**
         * Создаёт исключение с указанием текстового сообщения об ошибке аутентификации.
         *
         * @param message описание причины сбоя аутентификации
         */
        public HotelAuthenticationException(String message) {
                super(new ErrorMessage(HttpStatus.UNAUTHORIZED, message));
        }
        /**
         * Создаёт исключение на основе готового объекта {@link ErrorMessage}.
         *
         * @param errorMessage объект с деталями ошибки (статус, сообщение и, опционально, временная метка)
         */
        public HotelAuthenticationException(ErrorMessage errorMessage) {
                super(errorMessage);
        }
}
