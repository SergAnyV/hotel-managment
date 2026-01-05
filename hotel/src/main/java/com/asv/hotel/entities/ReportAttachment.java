package com.asv.hotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * Сущность вложения (файла), прикреплённого к отчёту.
 * <p>
 * Хранит бинарное содержимое файла вместе с метаданными:
 * именем, MIME-типом, размером и привязкой к отчёту.
 * </p>
 * <p>
 * Используется для хранения изображений, сканов или других документов,
 * связанных с инцидентами или запросами в системе отеля.
 * </p>
 * <p>
 * Поддерживает автоматическую установку временной метки создания.
 * </p>
 */
@Entity
@Table(name = "report_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAttachment {

    /**
     * Уникальный идентификатор вложения.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя файла (без пути). Например: "photo.jpg".
     * Обязательное поле, до 30 символов.
     */
    @Column(name = "file_name", nullable = false, length = 30)
    private String fileName;

    /**
     * MIME-тип содержимого файла. Например: "image/jpeg", "application/pdf".
     * Обязательное поле, до 30 символов.
     */
    @Column(name = "content_type", nullable = false, length = 30)
    private String contentType;

    /**
     * Размер файла в байтах.
     * Обязательное поле.
     */
    @Column(name = "size", nullable = false, length = 20)
    private Long size;

    /**
     * Бинарное содержимое файла.
     * Хранится напрямую в БД (например, в колонке типа BYTEA или BLOB).
     * Обязательное поле.
     */
    @Column(name = "content", nullable = false)
    private byte[] content;

    /**
     * Дата и время загрузки вложения.
     * Устанавливается автоматически при сохранении и не изменяется впоследствии.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Отчёт, к которому прикреплено вложение.
     * Обязательная связь "много-к-одному". Загружается лениво (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

}
