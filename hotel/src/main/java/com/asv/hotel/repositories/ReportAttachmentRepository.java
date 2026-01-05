package com.asv.hotel.repositories;

import com.asv.hotel.entities.ReportAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления вложениями отчётов ({@link ReportAttachment}).
 * <p>
 * Предоставляет методы для:
 * <ul>
 *   <li>поиска вложений по идентификатору отчёта;</li>
 *   <li>удаления вложения по его ID;</li>
 *   <li>стандартной загрузки по идентификатору вложения.</li>
 * </ul>
 * </p>
 * <p>
 * Все кастомные запросы реализованы через native SQL.
 * </p>
 */
@Repository
public interface ReportAttachmentRepository extends JpaRepository<ReportAttachment, Long> {
    /**
     * Находит вложение по его уникальному идентификатору.
     * <p>
     * Метод переопределяет стандартный из {@link JpaRepository} без изменения поведения.
     * Может быть удалён, если не требуется специфическая логика.
     * </p>
     *
     * @param id идентификатор вложения
     * @return {@link Optional} с вложением или пустой, если не найдено
     */
    @Override
    Optional<ReportAttachment> findById(Long id);

    /**
     * Возвращает все вложения, привязанные к отчёту с указанным ID.
     *
     * @param id идентификатор отчёта
     * @return список вложений (может быть пустым)
     */
    @Query(value = """
            SELECT * FROM report_attachments 
            WHERE report_id=:id
            """, nativeQuery = true)
    List<ReportAttachment> findReportAttachmentByReportID(@Param("id") Long id);

    /**
     * Удаляет вложение по его уникальному идентификатору.
     *
     * @param id идентификатор вложения
     * @return количество удалённых строк (обычно 0 или 1)
     */
    @Modifying
    @Query(value = "DELETE FROM report_attachments WHERE id=:id", nativeQuery = true)
    int deleteReportAttachmentById(@Param("id") Long id);

    /**
     * Возвращает все вложения для указанного отчёта.
     * <p>
     * Фактически дублирует метод {@link #findReportAttachmentByReportID(Long)}.
     * Рекомендуется использовать один метод для избежания дублирования.
     * </p>
     *
     * @param reportId идентификатор отчёта
     * @return список вложений (может быть пустым)
     */
    @Query(value = "SELECT * FROM report_attachments WHERE report_id =:report_id", nativeQuery = true)
    List<ReportAttachment> findReportAttachmentForZipListByReportId(@Param("report_id") Long reportId);
}
