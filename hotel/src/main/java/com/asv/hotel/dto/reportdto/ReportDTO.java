package com.asv.hotel.dto.reportdto;


import com.asv.hotel.entities.enums.ReportStatus;
import com.asv.hotel.entities.enums.ReportType;
import lombok.*;

import java.time.LocalDate;

/**
 * Объект передачи данных (DTO), представляющий отчёт в системе управления отелем.
 * <p>
 * Используется для передачи информации об отчёте между слоями приложения
 * (например, от сервиса к контроллеру или клиенту) без раскрытия внутренней структуры сущности.
 * Содержит основные данные об отчёте: статус, тип, даты создания и обновления,
 * номер комнаты и имя сотрудника, создавшего отчёт.
 * </p>
 *
 * <p><b>Примечание:</b> Для полей даты используются значения типа {@link java.time.LocalDate}
 * (без времени), так как в интерфейсе отображается только календарная дата, а не точное время.</p>
 *
 * @see com.asv.hotel.entities.Report
 * @see com.asv.hotel.entities.enums.ReportStatus
 * @see com.asv.hotel.entities.enums.ReportType
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    /**
     * Уникальный идентификатор отчёта.
     * <p>
     * Соответствует полю {@code id} сущности {@link com.asv.hotel.entities.Report}.
     */
    private Long id;
    /**
     * Текущий статус отчёта (например, ВЫДАН, В_РАБОТЕ, РЕШЁН).
     * <p>
     * Представлен в виде перечисления для обеспечения типобезопасности
     * и ограничения допустимых значений.
     *
     * @see com.asv.hotel.entities.enums.ReportStatus
     */
    private ReportStatus reportStatus;
    /**
     * Человекочитаемое описание статуса отчёта.
     * <p>
     * Автоматически заполняется на основе метода {@link com.asv.hotel.entities.enums.ReportStatus#getDescription()}
     * при маппинге из сущности.
     */
    private String descriptionStatus;
    /**
     * Тип или категория отчёта (например, ISSUE, WORK).
     * <p>
     * Определяется перечислением, чтобы гарантировать использование только заранее заданных значений.
     *
     * @see com.asv.hotel.entities.enums.ReportType
     */
    private ReportType reportType;
    /**
     * Человекочитаемое описание типа отчёта.
     * <p>
     * Заполняется автоматически из {@link com.asv.hotel.entities.enums.ReportType#getDescription()}
     * при преобразовании сущности в DTO.
     */
    private String descriptionType;
    /**
     * Дата создания отчёта.
     * <p>
     * Хранится как {@link java.time.LocalDate}, то есть содержит только дату .
     */
    private LocalDate createdAt;
    /**
     * Дата последнего обновления отчёта.
     * <p>
     * Также представлена в виде {@link java.time.LocalDate} для единообразия отображения.
     */
    private LocalDate updatedDate;
    /**
     * Номер комнаты, к которой относится отчёт.
     * <p>
     * Извлекается из связанной сущности {@link com.asv.hotel.entities.Room}.
     */
    private String roomNumber;
    /**
     * Имя пользователя (никнейм) сотрудника, создавшего отчёт.
     * <p>
     * Получается из поля {@code nickName} связанной сущности {@link com.asv.hotel.entities.User}.
     */
    private String ownerNickName;

}
