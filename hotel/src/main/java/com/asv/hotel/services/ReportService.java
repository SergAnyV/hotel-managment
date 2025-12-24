package com.asv.hotel.services;

import com.asv.hotel.dto.reportdto.ReportDTO;
import com.asv.hotel.entities.enums.ReportType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
/**
 * Сервис для управления отчётами в системе отеля.
 * <p>
 * Предоставляет методы для создания отчётов, добавления вложений к существующим отчётам
 * и удаления отдельных вложений с учётом прав доступа.
 * </p>
 * <p>
 * Все операции, изменяющие состояние данных (создание, добавление файлов, удаление),
 * выполняются в рамках транзакций и включают валидацию входных данных и проверку прав пользователя.
 * </p>
 */
public interface ReportService {
    /**
     * Создаёт новый отчёт указанного типа для заданной комнаты.
     * <p>
     * Отчёт автоматически привязывается к текущему авторизованному пользователю (сотруднику).
     * При наличии файлов они преобразуются во вложения и сохраняются вместе с отчётом.
     * </p>
     *
     * @param reportType        тип отчёта (например, жалоба, техническая неисправность)
     * @param roomNumber        номер комнаты, к которой относится отчёт
     * @param multipartFileList список загружаемых файлов (может быть пустым или {@code null})
     * @return DTO созданного отчёта или {@code null}, если комната с указанным номером не найдена
     */
    ReportDTO createReport(ReportType reportType, String roomNumber, List<MultipartFile> multipartFileList);
    /**
     * Добавляет одно или несколько вложений к существующему отчёту.
     * <p>
     * Требуется, чтобы отчёт с указанным ID существовал.
     * Поддерживаются только файлы допустимых форматов (проверяется внутри сервиса вложений).
     * </p>
     *
     * @param reportId          идентификатор отчёта
     * @param multipartFileList список файлов для прикрепления (не может быть пустым или {@code null})
     * @throws com.asv.hotel.exceptions.HotelDataNotFoundException если отчёт не найден
     * @throws com.asv.hotel.exceptions.HotelIncorrectInputData   если файлы отсутствуют или имеют недопустимый формат
     */
    void addReportAttachmentToReport(Long reportId, List<MultipartFile> multipartFileList);
    /**
     * Удаляет конкретное вложение из отчёта по идентификаторам отчёта и вложения.
     * <p>
     * Удаление разрешено только:
     * <ul>
     *   <li>администраторам и менеджерам системы,</li>
     *   <li>сотруднику, создавшему данный отчёт.</li>
     * </ul>
     * </p>
     *
     * @param reportId           идентификатор отчёта
     * @param reportAttachmentId идентификатор удаляемого вложения
     * @throws com.asv.hotel.exceptions.HotelDataNotFoundException если отчёт или вложение не найдены,
     *                                                             либо у пользователя нет прав на удаление
     * @throws com.asv.hotel.exceptions.HotelIncorrectInputData   если вложение не принадлежит указанному отчёту
     */
    void deleteAttachmentFromReport(Long reportId,Long reportAttachmentId);
}
