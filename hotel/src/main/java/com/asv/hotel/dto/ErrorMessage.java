package com.asv.hotel.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
/**
 * DTO для стандартизированного представления ошибок.
 * <p>
 * Используется в глобальных обработчиках исключений ({@code @ControllerAdvice})
 * для формирования единообразного JSON-ответа при возникновении ошибок.
 * </p>
 * <p>
 * Содержит HTTP-статус и сообщение об ошибке,
 * что упрощает диагностику проблем как для клиентских приложений, так и для разработчиков.
 * </p>
 */
@AllArgsConstructor
@Getter
public class ErrorMessage {
    /**
     * HTTP-статус, соответствующий типу ошибки.
     * <p>
     * Например: {@link HttpStatus#NOT_FOUND}, {@link HttpStatus#BAD_REQUEST}, {@link HttpStatus#INTERNAL_SERVER_ERROR}.
     * </p>
     */
    private HttpStatus httpStatus;
    /**
     * Описание ошибки.
     * <p>
     * Сообщение должно быть понятным конечному пользователю или разработчику клиента,
     * но не должно раскрывать внутренние детали реализации (во избежание уязвимостей).
     * </p>
     */
    private String message;

}
