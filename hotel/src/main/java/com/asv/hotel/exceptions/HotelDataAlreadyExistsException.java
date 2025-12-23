package com.asv.hotel.exceptions;

import com.asv.hotel.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
/**
 * Исключение, возникающее при попытке создать сущность, которая уже существует в системе.
 * <p>
 * Выбрасывается, когда операция создания (например, регистрация пользователя, добавление номера отеля)
 * нарушает ограничение уникальности — например, дублируется email, номер комнаты, логин и т.д.
 * </p>
 * <p>
 * Сопоставляется с HTTP-статусом {@link HttpStatus#CONFLICT} (409),
 * что соответствует стандарту REST для ситуаций конфликта при создании ресурса.
 * </p>
 */
public class HotelDataAlreadyExistsException extends HotelMainException {
    /**
     * Создаёт исключение с указанием текстового сообщения об ошибке.
     *
     * @param message описание конфликта (например, "Пользователь с таким email уже существует")
     */
    public HotelDataAlreadyExistsException(String message) {
        super(new ErrorMessage(HttpStatus.CONFLICT, message));
    }
    /**
     * Создаёт исключение с форматированным сообщением, указывающим тип ресурса и идентификатор,
     * который уже занят.
     *
     * @param resourceType тип сущности (например, "User", "Room", "Booking")
     * @param identifier   уникальный идентификатор или значение, вызвавшее конфликт (например, email, номер комнаты)
     */
    public HotelDataAlreadyExistsException(String resourceType, String identifier) {
        super(new ErrorMessage(
                HttpStatus.CONFLICT,
                String.format("%s already exists with identifier: %s", resourceType, identifier)
        ));
    }
    /**
     * Создаёт исключение на основе готового объекта {@link ErrorMessage}.
     *
     * @param errorMessage объект с деталями ошибки (статус, сообщение и, опционально, временная метка)
     */
    public HotelDataAlreadyExistsException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
