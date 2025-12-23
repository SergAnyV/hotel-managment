package com.asv.hotel.controllers;

import com.asv.hotel.dto.reportdto.ReportDTO;
import com.asv.hotel.entities.enums.ReportType;
import com.asv.hotel.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
/**
 * Контроллер для управления отчётами через REST API.
 * <p>
 * Предоставляет эндпоинты для:
 * <ul>
 *   <li>Создания нового отчёта с возможностью прикрепления файлов</li>
 *   <li>Добавления дополнительных вложений к существующему отчёту</li>
 *   <li>Удаления отдельного вложения из отчёта</li>
 * </ul>
 * <p>
 * Все операции автоматически привязываются к текущему авторизованному пользователю
 * (извлекается через Spring Security Context).
 * Права доступа на удаление вложений проверяются на уровне сервиса:
 * разрешено только владельцу отчёта или пользователям с ролями ADMIN/MANAGER.
 * <p>
 * Запросы с файлами должны использовать тип контента {@code multipart/form-data}.
 * <p>
 * Ответы соответствуют REST-конвенциям:
 * <ul>
 *   <li>{@code 200 OK} — успешное создание отчёта с телом (DTO)</li>
 *   <li>{@code 204 No Content} — успешное добавление/удаление без тела</li>
 *   <li>{@code 400 Bad Request} — ошибка валидации или несуществующая комната</li>
 *   <li>{@code 404 Not Found} — отчёт или вложение не найдены</li>
 * </ul>
 *
 * @see ReportService — сервисный слой, реализующий бизнес-логику и проверку прав доступа
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;
    /**
     * Создаёт новый отчёт указанного типа для заданной комнаты.
     * <p>
     * Отчёт автоматически привязывается к текущему авторизованному пользователю.
     * Поддерживается прикрепление одного или нескольких файлов (изображения, документы и т.д.).
     * <p>
     * Если комната с указанным номером не существует, возвращается ошибка {@code 400 Bad Request}.
     * <p>
     * Файлы не обязательны — отчёт может быть создан без вложений.
     *
     * @param reportType        тип отчёта (обязательный параметр формы)
     * @param roomNumber        номер комнаты (макс. 10 символов, обязательный)
     * @param multipartFileList список файлов для прикрепления (необязательный параметр)
     * @return {@link ResponseEntity} с DTO созданного отчёта ({@code 200 OK}) или {@code 400 Bad Request},
     *         если комната не найдена
     *
     * @see ReportService#createReport(ReportType, String, List)
     */
    @Operation(
            summary = "Создать новый отчёт",
            description = "Создаёт отчёт указанного типа для заданной комнаты. " +
                    "Автоматически привязывается к текущему пользователю. " +
                    "Поддерживается прикрепление файлов через multipart/form-data. " +
                    "Если комната не найдена — возвращается 400."
    )
    @ApiResponse(responseCode = "200", description = "Отчёт успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные: несуществующая комната или нарушение валидации")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReportDTO> createReport(
            @RequestParam("reportType")
            @NotNull
            ReportType reportType,
            @RequestParam("roomNumber")
            @NotNull
            @Size(max = 10)
            String roomNumber,
            @RequestParam(value = "multipartFileList", required = false)
            List<MultipartFile> multipartFileList) {
        ReportDTO reportDTO = reportService.createReport(reportType, roomNumber, multipartFileList);
        if (reportDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(reportDTO);
    }
    /**
     * Добавляет одно или несколько вложений к существующему отчёту.
     * <p>
     * Требуется, чтобы отчёт с указанным ID существовал.
     * Список файлов обязателен и не может быть пустым.
     * Поддерживаются только файлы допустимых форматов (проверяется в сервисе).
     * <p>
     * Операция разрешена только:
     * <ul>
     *   <li>Владельцу отчёта (сотруднику, создавшему его), ИЛИ</li>
     *   <li>Пользователям с ролями ADMIN или MANAGER</li>
     * </ul>
     *
     * @param reportId          идентификатор существующего отчёта (в пути URL)
     * @param multipartFileList непустой список файлов для прикрепления (обязательный параметр формы)
     * @return {@link ResponseEntity} с кодом {@code 204 No Content} при успехе
     * @throws com.asv.hotel.exceptions.HotelDataNotFoundException если отчёт не найден
     * @throws com.asv.hotel.exceptions.HotelIncorrectInputData   если файлы отсутствуют или недопустимы
     *
     * @see ReportService#addReportAttachmentToReport(Long, List)
     */
    @Operation(
            summary = "Добавить вложения к отчёту",
            description = "Прикрепляет один или несколько файлов к существующему отчёту. " +
                    "Требуется, чтобы отчёт существовал. " +
                    "Доступ разрешён только владельцу отчёта или пользователям с ролями ADMIN/MANAGER."
    )
    @ApiResponse(responseCode = "204", description = "Вложения успешно добавлены")
    @ApiResponse(responseCode = "400", description = "Отсутствуют файлы или они имеют недопустимый формат")
    @ApiResponse(responseCode = "404", description = "Отчёт с указанным ID не найден")
    @PostMapping("/{reportId}/attachments")
    public ResponseEntity<Void> addReportAttachmentToTheReportByRID(@PathVariable("reportId")
                                                                    @NotNull
                                                                    @NumberFormat
                                                                    @Positive
                                                                    Long reportId,
                                                                    @RequestParam(value = "multipartFileList", required = false)
                                                                    @NotNull
                                                                    List<MultipartFile> multipartFileList) {
        reportService.addReportAttachmentToReport(reportId, multipartFileList);
        return ResponseEntity.noContent().build();
    }
    /**
     * Удаляет конкретное вложение из отчёта по идентификаторам отчёта и вложения.
     * <p>
     * Удаление разрешено только:
     * <ul>
     *   <li>Администраторам и менеджерам системы,</li>
     *   <li>Сотруднику, создавшему данный отчёт.</li>
     * </ul>
     * <p>
     * Если вложение не принадлежит указанному отчёту или не существует — возвращается ошибка {@code 404}.
     *
     * @param reportId           идентификатор отчёта (в пути URL)
     * @param reportAttachmentId идентификатор удаляемого вложения (в пути URL)
     * @return {@link ResponseEntity} с кодом {@code 204 No Content} при успешном удалении
     * @throws com.asv.hotel.exceptions.HotelDataNotFoundException если отчёт/вложение не найдены или нет прав
     *
     * @see ReportService#deleteAttachmentFromReport(Long, Long)
     */
    @Operation(
            summary = "Удалить вложение из отчёта",
            description = "Удаляет конкретное вложение из отчёта по ID отчёта и ID вложения. " +
                    "Доступ разрешён только владельцу отчёта или пользователям с ролями ADMIN/MANAGER."
    )
    @ApiResponse(responseCode = "204", description = "Вложение успешно удалено")
    @ApiResponse(responseCode = "404", description = "Отчёт или вложение не найдены, либо у пользователя нет прав")
    @DeleteMapping("/{reportId}/{reportAttachmentId}")
    public ResponseEntity<Void> deleteAttachFromReportById(@PathVariable("reportId")
                                                           @NotNull
                                                           @NumberFormat
                                                           @Positive
                                                           Long reportId,
                                                           @PathVariable("reportAttachmentId")
                                                           @NotNull
                                                           @NumberFormat
                                                           @Positive
                                                           Long reportAttachmentId) {

        reportService.deleteAttachmentFromReport(reportId, reportAttachmentId);
        return ResponseEntity.noContent().build();
    }

}
