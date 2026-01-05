package com.asv.hotel.entities;

import com.asv.hotel.entities.enums.ReportStatus;
import com.asv.hotel.entities.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность отчёта (репорта) в системе отеля.
 * <p>
 * Представляет инцидент, запрос или служебную запись, связанную с конкретным номером
 * и созданным сотрудником (staff).
 * </p>
 * <p>
 * Отчёт содержит:
 * <ul>
 *   <li>статус (например: НОВЫЙ, В_РАБОТЕ, ЗАКРЫТ);</li>
 *   <li>тип (например: ПОЛОМКА, УБОРКА, ЖАЛОБА);</li>
 *   <li>автоматически генерируемые текстовые описания статуса и типа;</li>
 *   <li>связь с комнатой и сотрудником;</li>
 *   <li>набор вложений (фото, документы).</li>
 * </ul>
 * </p>
 * <p>
 * Поддерживает автоматическое обновление временных меток создания и изменения,
 * а также удобный метод {@link #addAttachment(ReportAttachment)} для управления вложениями.
 * </p>
 */
@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    /**
     * Уникальный идентификатор отчёта.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текущий статус отчёта (например: NEW, IN_PROGRESS, RESOLVED).
     * Обязательное поле, хранится как строка.
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    /**
     * Текстовое описание статуса (например: "Новый отчёт").
     * Автоматически заполняется из {@link ReportStatus#getDescription()} при сохранении или обновлении.
     */
    @Column(name = "description_status", length = 50)
    private String descriptionStatus;

    /**
     * Тип отчёта (например: MAINTENANCE, CLEANING, COMPLAINT).
     * Обязательное поле, хранится как строка, до 20 символов.
     */
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ReportType reportType;

    /**
     * Текстовое описание типа отчёта (например: "Техническая неисправность").
     * Автоматически заполняется из {@link ReportType#getDescription()} при сохранении или обновлении.
     */
    @Column(name = "description_type", length = 50)
    private String descriptionType;

    /**
     * Дата и время создания отчёта.
     * Устанавливается автоматически при первом сохранении и не изменяется.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления отчёта.
     * Обновляется автоматически при каждом изменении.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedDate;

    /**
     * Номер отеля, к которому относится отчёт.
     * Обязательная связь. Загружается лениво (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /**
     * Сотрудник, создавший отчёт.
     * Обязательная связь. Загружается лениво (LAZY).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    /**
     * Набор вложений (файлов), прикреплённых к отчёту.
     * Каскадное удаление и обновление включено; "сиротские" записи удаляются автоматически.
     * Инициализируется пустым {@link HashSet} для безопасной работы без null-проверок.
     */
    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ReportAttachment> reportAttachmentSet = new HashSet<>();

    /**
     * Колбэк JPA: синхронизирует текстовые описания {@code descriptionType} и {@code descriptionStatus}
     * с описаниями из соответствующих перечислений {@link ReportType} и {@link ReportStatus}
     * перед сохранением или обновлением сущности.
     */
    @PrePersist
    @PreUpdate
    private void preUpdate() {
        if (reportType != null) {
            this.descriptionType = reportType.getDescription();
        }
        if (reportStatus != null) {
            this.descriptionStatus = reportStatus.getDescription();
        }
    }

    /**
     * Добавляет вложение к отчёту и устанавливает двунаправленную связь.
     *
     * @param attachment вложение, которое будет привязано к этому отчёту
     */
    public void addAttachment(ReportAttachment attachment) {
        attachment.setReport(this);
        this.reportAttachmentSet.add(attachment);
    }

}
