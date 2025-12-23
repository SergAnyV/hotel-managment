package com.asv.hotel.exceptions;

import com.asv.hotel.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
/**
        * Базовый класс для всех кастомных исключений в системе управления отелем.
        * <p>
 * Предназначен для унификации обработки ошибок: каждое исключение содержит
 * структурированное сообщение об ошибке ({@link ErrorMessage}), включающее
 * HTTP-статус и текстовое описание. Это позволяет централизованно обрабатывать
 * исключения и возвращать клиенту согласованный формат ошибки.
        * </p>
        * <p>
 * Является unchecked-исключением (наследуется от {@link RuntimeException}),
          * </p>
        */
public class HotelMainException extends RuntimeException {
    private final ErrorMessage errorMessage;
    /**
     * Создаёт исключение на основе объекта {@link ErrorMessage}.
     *
     * @param errorMessage объект с деталями ошибки, включая HTTP-статус и сообщение
     */
    public HotelMainException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
    /**
     * Создаёт исключение на основе объекта {@link ErrorMessage} и первопричины (cause).
     * <p>
     * Используется, когда необходимо сохранить цепочку исключений (например, при оборачивании
     * исключения из нижележащего слоя).
     *
     * @param errorMessage объект с деталями ошибки
     * @param cause        первопричина (исходное исключение)
     */
    public HotelMainException(ErrorMessage errorMessage, Throwable cause) {
        super(errorMessage.getMessage(), cause);
        this.errorMessage = errorMessage;
    }
    /**
     * Возвращает структурированное сообщение об ошибке.
     *
     * @return объект {@link ErrorMessage}, содержащий статус, сообщение и метаданные
     */
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
    /**
     * Возвращает HTTP-статус, связанный с данным исключением.
     * <p>
     * Метод для быстрого доступа к статусу без обращения к {@link ErrorMessage}.
     *
     * @return HTTP-статус
     */
    public HttpStatus getHttpStatus() {
        return errorMessage.getHttpStatus();
    }

}
