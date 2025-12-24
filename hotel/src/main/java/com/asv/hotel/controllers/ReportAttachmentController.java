package com.asv.hotel.controllers;

import com.asv.hotel.dto.ErrorMessage;
import com.asv.hotel.dto.reportattachmendto.ReportAttachmentSimpleDTO;
import com.asv.hotel.exceptions.HotelReportAttachmentException;
import com.asv.hotel.services.ReportAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления вложениями отчётов через REST API.
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Получения отдельного вложения по его идентификатору (в виде бинарных данных)</li>
 *   <li>Скачивания всех вложений отчёта в виде ZIP-архива</li>
 *   <li>Удаления вложения по идентификатору</li>
 * </ul>
 * <p>
 * Все операции защищены механизмами Spring Security:
 * <ul>
 *   <li>Получение вложений разрешено только владельцу отчёта (staff) или пользователям с ролями ADMIN/MANAGER</li>
 *   <li>Удаление разрешено только пользователям с ролями ADMIN/MANAGER</li>
 * </ul>
 * <p>
 * Ответы возвращаются в соответствии с REST-конвенциями:
 * <ul>
 *   <li>{@code 200 OK} — успешное выполнение с телом ответа</li>
 *   <li>{@code 204 No Content} — успешное удаление без тела</li>
 *   <li>{@code 404 Not Found} — запрашиваемый ресурс не существует или недоступен</li>
 * </ul>
 *
 * @see ReportAttachmentService — сервисный слой, обеспечивающий бизнес-логику и проверку прав доступа
 */
@Tag(name = "ReportAttachment Management", description = "REST API для управлениями вложенным файлам к отчету")
@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class ReportAttachmentController {
    private final ReportAttachmentService reportAttachmentService;

    /**
     * Возвращает содержимое вложения по его идентификатору в виде бинарных данных.
     * <p>
     * Поддерживает отображение изображений непосредственно в браузере (благодаря заголовку {@code Content-Disposition: inline}).
     * MIME-тип определяется на основе сохранённого типа файла.
     * <p>
     * Доступ разрешён только:
     * <ul>
     *   <li>Владельцу отчёта (сотруднику, создавшему отчёт), ИЛИ</li>
     *   <li>Пользователям с ролью ADMIN или MANAGER</li>
     * </ul>
     *
     * @param id идентификатор вложения
     * @return {@link ResponseEntity} с бинарным содержимым файла и соответствующими заголовками
     * @throws HotelReportAttachmentException если вложение не найдено или у пользователя нет прав доступа (возвращает HTTP 404)
     *
     * @see ReportAttachmentService#findReportAttachmentSimpleDTOByID(Long)
     */
    @Operation(summary = "Найти вложение его id",
            description = "Возвращает содержимое вложения (изображение )в виде бинарных данных. " +
                    "Тип содержимого определяется MIME-типом файла. " +
                    "Поддерживается отображение в браузере (inline) для изображений.")
    @ApiResponse(responseCode = "200", description = "Вложение успешно найдено и возвращено")
    @ApiResponse(responseCode = "404", description = "вложение не найдены")
    @GetMapping("/id/{id}")
    public ResponseEntity<Resource> getContentByAttachmentID(@PathVariable(value = "id")
                                                             @NotNull
                                                             @Positive
                                                             Long id) {
        ReportAttachmentSimpleDTO resource = reportAttachmentService.findReportAttachmentSimpleDTOByID(id);
        if (resource == null) {
            throw new HotelReportAttachmentException(new ErrorMessage(HttpStatus.NOT_FOUND,
                    "Некорректный запрос по роли и владельце приложенного " +
                            "файла, запросил владелец "));
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("inline; filename=\"%s\"", resource.getFileName()))
                .body(resource.getByteArrayResource());
    }

    /**
     * Формирует и возвращает ZIP-архив со всеми вложениями отчёта с указанным ID.
     * <p>
     * Архив предназначен для скачивания (заголовок {@code Content-Disposition: attachment}).
     * <p>
     * Доступ разрешён только:
     * <ul>
     *   <li>Владельцу отчёта (сотруднику, создавшему отчёт), ИЛИ</li>
     *   <li>Пользователям с ролью ADMIN или MANAGER</li>
     * </ul>
     * <p>
     * Если у пользователя нет доступа или вложений нет, возвращается пустой массив байтов
     * (HTTP 200 с телом длиной 0), что позволяет избежать ошибок на клиенте.
     *
     * @param reportId идентификатор отчёта
     * @return {@link ResponseEntity} с байтовым представлением ZIP-архива
     *
     * @see ReportAttachmentService#findByteArrayAttachmentsLikeZipByReportID(Long)
     */
    @Operation(summary = "Найти архив с вложениями по id отчета",
            description = "Возвращает содержимое вложения (изображение )в виде бинарных данных. " +
                    "Поддерживается выгрузка из браузера (attachment) для архива.Только для владельцев отчета или ADMIN/MANAGER")
    @ApiResponse(responseCode = "200", description = "Вложения найдены и возвращены в виде ZIP-архива. " +
            "Если вложений нет или доступ запрещён, возвращается пустой архив (0 байт).")
    @GetMapping(value = "/{reportId}/attachments/zip-stream", produces = "application/zip")
    public ResponseEntity<byte[]> downloadAttachmentsAsZip(@PathVariable(value = "reportId")
                                                           @NotNull
                                                           @Positive
                                                           Long reportId) {
        byte[] response = reportAttachmentService.findByteArrayAttachmentsLikeZipByReportID(reportId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report_" + reportId + "_attachments.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response);
    }

    /**
     * Удаляет вложение по его идентификатору.
     * <p>
     * Операция защищена аннотацией {@link PreAuthorize} — доступ разрешён только пользователям
     * с ролями {@code ADMIN} или {@code MANAGER}.
     *
     * @param id идентификатор удаляемого вложения
     * @return {@link ResponseEntity} с кодом {@code 204 No Content} при успешном удалении
     * @throws com.asv.hotel.exceptions.HotelDataNotFoundException если вложение с указанным ID не существует
     *
     * @see ReportAttachmentService#deleteReportAttachmentById(Long)
     */
    @Operation(summary = "Удалить вложение",
            description = "удаляет данные существующего вложения по id")
    @ApiResponse(responseCode = "204", description = "Вложения удалено")
    @ApiResponse(responseCode = "404", description = "Вложения не найдено")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReportAttachment(@PathVariable(value = "id")
                                                       @Positive
                                                       @NotNull
                                                       Long id) {
        reportAttachmentService.deleteReportAttachmentById(id);
        return ResponseEntity.noContent().build();
    }

}
