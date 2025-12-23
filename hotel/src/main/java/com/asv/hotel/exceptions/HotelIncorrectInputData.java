package com.asv.hotel.exceptions;

import com.asv.hotel.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
/**
 * Исключение, представляющее ошибку некорректных входных данных при работе с сущностями отеля.
 * <p>
 * Выбрасывается, когда клиент передаёт данные в неверном формате, нарушает бизнес-правила
 * или предоставляет недопустимые значения .
 * </p>
 * <p>
 * По умолчанию сопоставляется с HTTP-статусом {@link HttpStatus#BAD_REQUEST} (400).
 * </p>
 */
public class HotelIncorrectInputData extends HotelMainException {
    /**
     * Создаёт исключение с указанием текстового сообщения об ошибке.
     *
     * @param message описание проблемы с входными данными
     */
    public HotelIncorrectInputData(String message) {
        super(
                new ErrorMessage(HttpStatus.BAD_REQUEST, message)
        );
    }
    /**
     * Создаёт исключение на основе готового объекта {@link ErrorMessage}.
     *
     * @param errorMessage объект с деталями ошибки
     */
    public HotelIncorrectInputData(ErrorMessage errorMessage) {
        super(errorMessage);
    }

    /**
     * Создаёт исключение с форматированным сообщением, указывающим ресурс и идентификатор,
     * для которых обнаружены некорректные данные.
     *
     * @param resource   тип сущности или ресурса (например, "User", "Room", "Booking")
     * @param identifier уникальный идентификатор или ключ, связанный с ошибкой (например, ID, email, номер комнаты)
     */
    public HotelIncorrectInputData(String resource, String idntifier) {
        super(new ErrorMessage(HttpStatus.BAD_REQUEST,
                String.format("%s Incorrect data format %s ", resource, idntifier))
        );
    }
}
