package com.asv.hotel.dto.reportattachmendto;

import lombok.*;
import org.springframework.core.io.ByteArrayResource;

/**
 * Data Transfer Object (DTO) для передачи бинарного содержимого вложения отчёта.
 * <p>
 * Используется в сценариях, где необходимо вернуть сам файл (например, при скачивании
 * или отображении изображения в браузере).
 * <p>
 * Содержит:
 * <ul>
 *   <li>Имя файла — для формирования заголовка {@code Content-Disposition}</li>
 *   <li>MIME-тип — для заголовка {@code Content-Type}</li>
 *   <li>Бинарные данные — обёрнутые в {@link ByteArrayResource} для совместимости с Spring MVC</li>
 * </ul>
 * <p>
 * Не содержит метаданных вроде ID или даты создания — только то, что нужно для HTTP-ответа.
 *
 * @see ReportAttachmentDTO — для передачи метаданных без содержимого
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReportAttachmentSimpleDTO {

    /**
     * Имя файла, используемое для формирования заголовка {@code Content-Disposition}.
     */
    private String fileName;

    /**
     * MIME-тип содержимого, используемый для заголовка {@code Content-Type}.
     * Пример: {@code "image/png"}.
     */
    private String contentType;

    /**
     * Бинарное содержимое файла, обёрнутое в {@link ByteArrayResource}.
     * Гарантирует корректную передачу через Spring MVC .
     * <p>
     * Всегда должен содержать непустой массив байтов (длина > 0).
     */
    private ByteArrayResource byteArrayResource;


}
