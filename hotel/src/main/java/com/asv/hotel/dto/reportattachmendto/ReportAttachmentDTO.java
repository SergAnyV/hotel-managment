package com.asv.hotel.dto.reportattachmendto;

import lombok.*;
/**
 * Data Transfer Object (DTO) для представления метаданных вложения отчёта.
 * <p>
 * Используется для передачи основной информации о вложении (без содержимого файла)
 * между слоями приложения.
 * <p>
 * Содержит только метаданные: имя файла, тип содержимого, размер и дату создания.
 * Бинарные данные файла ({@code content}) в этом DTO не включены.
 *
 * @see ReportAttachmentSimpleDTO — для передачи бинарного содержимого файла
 */

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReportAttachmentDTO {
    /**
     * Уникальный идентификатор вложения.
     */
    private Long id;
    /**
     * Дата и время создания вложения.
     * для упрощения отображения (без времени).
     */
    private LocalDate createdAt;
    /**
     * Имя файла, под которым он был загружен.
     * Не должно быть {@code null} или пустым.
     * Пример: {@code "photo.jpg"}.
     */
    private String fileName;
    /**
     * MIME-тип содержимого файла.
     * Не должен быть {@code null} или пустым.
     * Пример: {@code "image/jpeg"}, {@code "application/pdf"}.
     */
    private String contentType;
    /**
     * Размер файла в байтах.
     * Всегда положительное число.
     */
    private Long size;


}
