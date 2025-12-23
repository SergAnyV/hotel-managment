package com.asv.hotel.exceptions;

import com.asv.hotel.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
/**
 * Исключение, возникающее при ошибках, связанных с отправкой или обработкой уведомлений в системе отеля.
 * <p>
 * Выбрасывается, когда происходит сбой в процессе генерации, доставки уведомлений
 * (например, ошибка подключения к SMTP-серверу, недоступность сервиса сообщений и т.д.).
 * </p>
 * <p>
 * По умолчанию сопоставляется с HTTP-статусом {@link HttpStatus#INTERNAL_SERVER_ERROR} (500),
 * так как ошибка связана с внутренней инфраструктурой приложения, а не с действиями клиента.
 * </p>
 */
public class HotelNotificationException extends HotelMainException {

    /**
     * Создаёт исключение с указанием текстового сообщения об ошибке.
     *
     * @param message описание проблемы, связанной с уведомлением
     */
    public HotelNotificationException(String message) {
        super(new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,message
        ));
    }

    /**
     * Создаёт исключение на основе готового объекта {@link ErrorMessage}.
     *
     * @param errorMessage объект с деталями ошибки (статус, сообщение и, опционально, временная метка)
     */
    public HotelNotificationException(ErrorMessage errorMessage) {
        super(errorMessage);
    }

    /**
     * Создаёт исключение с форматированным сообщением, указывающим ресурс и идентификатор,
     * для которых произошла ошибка уведомления.
     *
     * @param resource   тип сущности или ресурса (например, "Booking", "User")
     * @param identifier уникальный идентификатор, связанный с ошибкой (например, ID бронирования, email пользователя)
     */
    public HotelNotificationException(String resource, String idntifier) {
        super(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("%s Notification Error %s ", resource, idntifier))
        );
    }
}
