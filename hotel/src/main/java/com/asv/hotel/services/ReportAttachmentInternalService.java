package com.asv.hotel.services;

import com.asv.hotel.entities.ReportAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
/**
 * Расширенный сервисный интерфейс для внутреннего использования в слое бизнес-логики.
 * <p>
 * Наследует все публичные методы из {@link ReportAttachmentService} и добавляет
 * дополнительные операции, необходимые для обработки файлов, генерации сущностей
 * и получения данных без учёта прав доступа (проверка прав выполняется в вызывающем коде).
 * <p>
 * Методы этого интерфейса предназначены для использования внутри сервисов и репозиториев,
 * но не должны вызываться напрямую из контроллеров.
 *
 * @see ReportAttachmentService — публичный API
 */
public interface ReportAttachmentInternalService extends ReportAttachmentService{

    /**
     * Находит все вложения, привязанные к отчёту с указанным ID.
     * <p>
     * Не выполняет проверку прав доступа — предполагается, что она сделана заранее.
     *
     * @param id идентификатор отчёта
     * @return список вложений, возможно пустой
     */
    List<ReportAttachment> findReportAttachmentByReportID(Long id);

    /**
     * Преобразует загруженный файл ({@link MultipartFile}) в сущность {@link ReportAttachment}.
     * <p>
     * Выполняет валидацию типа файла (только JPEG/PNG) и извлечение метаданных.
     *
     * @param multipartFile исходный загруженный файл
     * @return сущность вложения или {@code null}, если файл не прошёл валидацию
     */
    ReportAttachment generateReportAttachmentFromMultipartFile(MultipartFile multipartFile);

    /**
     * Преобразует список загруженных файлов в множество сущностей {@link ReportAttachment}.
     * <p>
     * Автоматически фильтрует недопустимые файлы (возвращаемые как {@code null}).
     *
     * @param multipartFileList список загруженных файлов
     * @return множество валидных сущностей вложений
     */
    Set<ReportAttachment> generateReportAttachmentSetFromMultipartFileList(List<MultipartFile> multipartFileList);

    /**
     * Находит все вложения отчёта с указанным ID, оптимизированные для формирования ZIP-архива.
     * <p>
     * Гарантирует, что связанные сущности (например, {@code report}) загружены полностью,
     * чтобы избежать LazyInitializationException при последующей обработке.
     *
     * @param id идентификатор отчёта
     * @return список вложений, готовых к использованию в архивации
     */
    List<ReportAttachment> findReportAttachmentForZipByReportID(Long id);
}
