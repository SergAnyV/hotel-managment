package com.asv.hotel.services;

import com.asv.hotel.dto.jobtypedto.JobTypeDTO;
import com.asv.hotel.dto.jobtypedto.JobTypeSimpleDTO;
import com.asv.hotel.dto.usertypedto.UserTypeDTO;
import com.asv.hotel.entities.JobType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Сервис для управления типами должностей сотрудников отеля.
 * <p>
 * Предоставляет методы для создания, поиска, удаления типов должностей,
 * а также для привязки ролей пользователей к должностям.
 * </p>
 * <p>
 * Все операции с текстовыми параметрами выполняются регистронезависимо.
 * </p>
 */
public interface JobTypeService {

    /**
     * Возвращает список всех типов должностей.
     *
     * @return список DTO всех типов должностей
     */
    List<JobTypeDTO> findAll();

    /**
     * Создаёт новый тип должности.
     *
     * @param jobTypeSimpleDTO данные для создания типа должности
     * @return DTO созданного типа
     */
    JobTypeDTO createJobType(JobTypeSimpleDTO jobTypeSimpleDTO);

    /**
     * Находит все типы должностей, название которых содержит указанную строку (регистронезависимо).
     *
     * @param title часть или полное название должности
     * @return список DTO найденных типов
     */
    List<JobTypeDTO> findJobTypesDTOByTitle(String title);

    /**
     * Находит все активные типы должностей, название которых содержит указанную строку (регистронезависимо).
     *
     * @param title часть или полное название должности
     * @return список DTO активных типов
     */
    List<JobTypeDTO> findActiveJobTypesDTOByTitle(String title);

    /**
     * Находит типы должностей по названию и статусу активности.
     *
     * @param title    часть или полное название должности
     * @param isActive строковое представление булева значения ("true"/"false")
     * @return список DTO типов, соответствующих критериям
     */
    List<JobTypeDTO> findJobTypesDTOByTitleAndActiveStatusWithoutUserType(String title, String isActive);

    /**
     * Удаляет тип должности по названию (регистронезависимо).
     *
     * @param title название типа должности
     */
    void deleteJobTypeByTitle(String tittle);

    /**
     * Привязывает роль пользователя к типу должности.
     * <p>
     * Добавляет указанную роль в набор ролей, которые могут занимать данную должность.
     * </p>
     *
     * @param jobTypeTitle название типа должности
     * @param userTypeRole название роли пользователя
     * @return множество DTO ролей, привязанных к должности после обновления
     */
    Set<UserTypeDTO> addUserTypeToJobType(String jobTypeTitle, String userTypeRole);
}
