package com.asv.hotel.exceptions;

import com.asv.hotel.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
/**
 * Исключение, возникающее при попытке доступа к сущности, которая не найдена в системе.
 * <p>
 * Выбрасывается, когда запрашиваемый ресурс (например, пользователь, номер отеля, бронирование)
 * отсутствует в базе данных по указанному идентификатору (ID, email, номер комнаты и т.д.).
 * </p>
 * <p>
 * Сопоставляется с HTTP-статусом {@link HttpStatus#NOT_FOUND} (404),
 * что соответствует стандарту REST для ситуаций, когда запрашиваемый ресурс не существует.
 * </p>
 */
public class HotelDataNotFoundException extends HotelMainException {
    /**
     * Создаёт исключение с указанием текстового сообщения об ошибке.
     *
     * @param message описание отсутствующего ресурса (например, "Пользователь не найден")
     */
    public HotelDataNotFoundException(String message) {
        super(new ErrorMessage(HttpStatus.NOT_FOUND, message));
    }
    /**
     * Создаёт исключение с форматированным сообщением, указывающим тип ресурса и идентификатор,
     * по которому не удалось найти данные.
     *
     * @param resourceType тип сущности (например, "User", "Room", "Booking")
     * @param identifier   идентификатор или уникальное значение, по которому выполнялся поиск (например, ID, email, номер комнаты)
     */
    public HotelDataNotFoundException(String resourceType, String identifier) {
        super(new ErrorMessage(
                HttpStatus.NOT_FOUND,
                String.format("%s not found with identifier: %s", resourceType, identifier)
        ));
    }

    /**
     * Создаёт исключение на основе готового объекта {@link ErrorMessage}.
     *
     * @param errorMessage объект с деталями ошибки (статус, сообщение и, опционально, временная метка)
     */
    public HotelDataNotFoundException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}



