package com.asv.hotel.services;

import com.asv.hotel.entities.JobType;

import java.util.List;

/**
 * Расширенный интерфейс сервиса управления типами должностей.
 * <p>
 * Предназначен для внутреннего использования в слое сервисов (а не в контроллерах).
 * Добавляет методы, возвращающие сущности {@link JobType} напрямую,
 * а также операции удаления и фильтрации по статусу активности.
 * </p>
 */
public interface JobTypeInternalService extends JobTypeService {
    /**
     * Находит все типы должностей, название которых содержит указанную строку (регистронезависимо).
     *
     * @param title часть или полное название должности
     * @return список сущностей типов должностей
     */
    List<JobType> findJobTypesByTitle(String title);

    /**
     * Находит все активные типы должностей, название которых содержит указанную строку (регистронезависимо).
     *
     * @param title часть или полное название должности
     * @return список активных сущностей типов должностей
     */
    List<JobType> findActiveJobTypesByTitle(String title);

    /**
     * Находит типы должностей по названию и статусу активности.
     *
     * @param title    часть или полное название должности
     * @param isActive строковое представление булева значения ("true"/"false")
     * @return список сущностей, соответствующих критериям
     */
    List<JobType> findJobTypesByTitleAndActiveStatusWithoutUserType(String title, String isActive);

    /**
     * Удаляет тип должности по уникальному идентификатору.
     *
     * @param id идентификатор типа должности
     */
    void deleteJobTypeById(Long id);

}
