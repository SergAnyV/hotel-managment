package com.asv.hotel.exceptions;

import com.asv.hotel.dto.ErrorMessage;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для всего веб-приложения системы управления отелем.
 * <p>
 * Централизованно перехватывает все необработанные исключения и преобразует их
 * в стандартизированные HTTP-ответы с кодом статуса и структурированным сообщением об ошибке.
 * </p>
 * <p>
 * Обрабатывает:
 * <ul>
 *   <li>Кастомные бизнес-исключения на основе {@link HotelMainException}</li>
 *   <li>Ошибки валидации входных данных ({@link MethodArgumentNotValidException})</li>
 *   <li>Проблемы безопасности: аутентификация ({@link AuthenticationException})
 *       и авторизация ({@link AccessDeniedException})</li>
 *   <li>Ошибки доступа к данным ({@link DataAccessException})</li>
 *   <li>Любые непредвиденные исключения ({@link Exception})</li>
 * </ul>
 * <p>
 * Все ошибки логируются с соответствующим уровнем:
 * <ul>
 *   <li>{@code WARN} — для клиентских ошибок (4xx)</li>
 *   <li>{@code ERROR} — для серверных ошибок (5xx)</li>
 * </ul>
 */
@Slf4j
@ControllerAdvice
public class HotelGlobalExceptionHandler {

    /**
     * Обрабатывает все кастомные исключения, наследуемые от {@link HotelMainException}.
     * <p>
     * Извлекает HTTP-статус и сообщение из встроенного {@link ErrorMessage}
     * и формирует соответствующий ответ. Логирует ошибку в зависимости от категории статуса.
     *
     * @param ex исключение типа {@link HotelMainException}
     * @return ответ с телом {@link ErrorMessage} и соответствующим HTTP-статусом
     */
    @ExceptionHandler(HotelMainException.class)
    public ResponseEntity<ErrorMessage> handleHotelMainException(HotelMainException ex) {
        ErrorMessage errorMessage = ex.getErrorMessage();
        if (ex.getHttpStatus().is4xxClientError()) {
            log.warn("Client error [{}]: {}", ex.getHttpStatus(), ex.getMessage());
        } else {
            log.error("Server error [{}]: {}", ex.getHttpStatus(), ex.getMessage());
        }
        return ResponseEntity.status(ex.getHttpStatus()).body(errorMessage);
    }

    /**
     * Обрабатывает ошибки доступа к базе данных.
     * <p>
     * Преобразует {@link DataAccessException} в ответ с кодом 500 и общим сообщением об ошибке БД.
     *
     * @param ex исключение доступа к данным
     * @return ответ с HTTP-статусом 500 и сообщением об ошибке
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorMessage> handleDataAccessException(DataAccessException ex) {
        return response500Error(ex, e -> "Database access error");
    }

    /**
     * Обрабатывает исключения, связанные с отказом в доступе (недостаточно прав).
     * <p>
     * Возвращает HTTP-статус 403 и сообщение о запрете доступа.
     *
     * @param ex исключение авторизации
     * @return ответ с HTTP-статусом 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.FORBIDDEN,
                "Access denied: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
    }

    /**
     * Обрабатывает ошибки аутентификации (неверные учётные данные, отсутствие токена и т.д.).
     * <p>
     * Возвращает HTTP-статус 401 и сообщение о необходимости аутентификации.
     *
     * @param ex исключение аутентификации
     * @return ответ с HTTP-статусом 401
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessage> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Unauthorized : {}", ex.getMessage());
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
    }

    /**
     * Обрабатывает ошибки валидации входящих DTO (аннотации Jakarta Bean Validation).
     * <p>
     * Собирает все сообщения об ошибках валидации в одну строку и возвращает ответ с кодом 400.
     *
     * @param ex исключение валидации
     * @return ответ с HTTP-статусом 400 и деталями ошибок
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<ErrorMessage> handleValidationExceptions(Exception ex) {
        log.warn("Validation failed: ");
        ErrorMessage error = new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                "Validation error: "
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Обрабатывает все остальные непредвиденные исключения.
     * <p>
     * Служит "последней линией обороны" для предотвращения утечки внутренних ошибок клиенту.
     * Всегда возвращает обобщённое сообщение с кодом 500.
     *
     * @param ex любое неперехваченное исключение
     * @return ответ с HTTP-статусом 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
        return response500Error(ex, e -> "Unexpected Internal Error ");
    }

    private ResponseEntity<ErrorMessage> response500Error(Exception ex, Function<Exception, String> function) {
        String errorMessage = function.apply(ex);
        log.error("{}: {}", errorMessage, ex.getMessage(), ex);
        ErrorMessage eMessage = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                errorMessage
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(eMessage);
    }

}