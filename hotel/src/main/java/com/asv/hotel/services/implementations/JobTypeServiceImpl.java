package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.jobtypedto.JobTypeDTO;
import com.asv.hotel.dto.jobtypedto.JobTypeSimpleDTO;
import com.asv.hotel.dto.mapper.JobTypeMapper;
import com.asv.hotel.dto.mapper.UserTypeMapper;
import com.asv.hotel.dto.usertypedto.UserTypeDTO;
import com.asv.hotel.entities.JobType;
import com.asv.hotel.entities.UserType;
import com.asv.hotel.exceptions.HotelDataAlreadyExistsException;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.repositories.JobTypeRepository;
import com.asv.hotel.services.JobTypeInternalService;

import com.asv.hotel.services.UserTypeInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления типами должностей сотрудников ({@link JobType}).
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>создание, поиск, обновление и удаление типов должностей;</li>
 *   <li>работу с привязкой ролей пользователей ({@link UserType}) к должностям;</li>
 *   <li>поиск по названию с поддержкой регистра и статуса активности.</li>
 * </ul>
 * </p>
 * <p>
 * Все входные строковые параметры автоматически приводятся к нижнему регистру и очищаются от пробелов
 * с помощью вспомогательного метода {@link #cleanString(String)}.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JobTypeServiceImpl implements JobTypeInternalService {
    private final JobTypeRepository jobTypeRepository;
    private final UserTypeInternalService userTypeInternalService;


    /**
     * Возвращает список всех типов должностей с инициализированными связями с ролями пользователей.
     *
     * @return список DTO всех типов должностей
     */
    @Transactional
    @Override
    public List<JobTypeDTO> findAll() {

        List<JobType> jobTypeList = jobTypeRepository.findAll();
        jobTypeList.forEach(jobType -> Hibernate.initialize(jobType.getUserTypes()));

        return jobTypeList.stream()
                .map(jp -> JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jp))
                .collect(Collectors.toList());
    }

    /**
     * Создаёт новый тип должности.
     * <p>
     * Новый тип автоматически помечается как активный ({@code isActive = true}).
     * В случае нарушения уникальности (например, дубликат названия) выбрасывается исключение.
     * </p>
     *
     * @param jobTypeSimpleDTO данные для создания типа должности
     * @return DTO созданного типа
     * @throws HotelDataAlreadyExistsException если тип с таким названием уже существует
     */
    @Transactional
    @Override
    public JobTypeDTO createJobType(JobTypeSimpleDTO jobTypeSimpleDTO) {
        JobType jobType = JobTypeMapper.INSTANCE.jobTypeSimpleDTOToJobType(jobTypeSimpleDTO);
        jobType.setIsActive(true);
        try {
            jobType = jobTypeRepository.save(jobType);
            JobTypeDTO result = JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jobType);
            return result;
        } catch (DataIntegrityViolationException ex) {
            log.error("Error: не пройдена проверка на уникальность полей jobType при сохранении в методе createJobType"
                    + "jobTypeSimpleDTO ={}", jobTypeSimpleDTO, ex);
            throw new HotelDataAlreadyExistsException(String.format(" Fileds is not unique for this jobTypeSimpleDTO= '%s' ",
                    jobTypeSimpleDTO));
        }
    }

    /**
     * Находит все типы должностей, название которых содержит указанную строку (регистронезависимо).
     *
     * @param title часть или полное название должности
     * @return список DTO найденных типов
     */
    @Transactional
    @Override
    public List<JobTypeDTO> findJobTypesDTOByTitle(String title) {
        title = cleanString(title);
        List<JobType> listJT = jobTypeRepository.findJobTypesByTitleIgnoreCase(title);

        return listJT.stream()
                .map(jobType -> JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jobType))
                .collect(Collectors.toList());

    }

    /**
     * Находит все активные типы должностей, название которых содержит указанную строку (регистронезависимо).
     *
     * @param title часть или полное название должности
     * @return список DTO активных типов
     */
    @Transactional
    @Override
    public List<JobType> findJobTypesByTitle(String title) {
        title = cleanString(title);

        List<JobType> listJT = jobTypeRepository.findJobTypesByTitleIgnoreCase(title);
        return listJT;

    }

    /**
     * Находит типы должностей по названию и статусу активности.
     *
     * @param title     часть или полное название должности
     * @return список DTO типов, соответствующих критериям
     */
    @Transactional
    @Override
    public List<JobTypeDTO> findActiveJobTypesDTOByTitle(String title) {
        title = cleanString(title);

        List<JobType> listJT = jobTypeRepository.findActiveJobTypesByTitleIgnoreCase(title);
        return listJT.stream()
                .map(jobType -> JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jobType))
                .collect(Collectors.toList());

    }

    /**
     * Находит типы должностей по названию и статусу активности.
     *
     * @param title     часть или полное название должности
     */
    @Transactional
    @Override
    public List<JobType> findActiveJobTypesByTitle(String title) {
        title = cleanString(title);

        List<JobType> listJT = jobTypeRepository.findActiveJobTypesByTitleIgnoreCase(title);
        return listJT;

    }

    /**
     * Находит типы должностей по названию и статусу активности.
     *
     * @param title     часть или полное название должности
     * @return список DTO типов, соответствующих критериям
     */
    @Transactional
    @Override
    public List<JobTypeDTO> findJobTypesDTOByTitleAndActiveStatusWithoutUserType(String title, String isActive) {
        isActive = cleanString(isActive);
        title = cleanString(title);
        Boolean isActiveBoolean = Boolean.valueOf(isActive);

        List<JobType> listJT = jobTypeRepository.findJobTypesByTitleAndActiveStatusWithoutUserType(title, isActiveBoolean);
        return listJT.stream()
                .map(jt -> JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jt))
                .collect(Collectors.toList());

    }

    /**
     * Находит типы должностей по названию и статусу активности.
     *
     * @param title     часть или полное название должности
     */
    @Transactional
    @Override
    public List<JobType> findJobTypesByTitleAndActiveStatusWithoutUserType(String title, String isActive) {
        Boolean isActiveBoolean = Boolean.valueOf(isActive);
        title = cleanString(title);
        List<JobType> listJT = jobTypeRepository.findJobTypesByTitleAndActiveStatusWithoutUserType(title, isActiveBoolean);
        return listJT;

    }

    /**
     * Удаляет тип должности по уникальному идентификатору.
     *
     * @param id идентификатор типа
     * @throws HotelDataNotFoundException если тип с указанным ID не найден
     */
    @Transactional
    @Override
    public void deleteJobTypeById(Long id) {
        if (jobTypeRepository.deleteJobTypeById(id) == 0) {
            log.error("Error: не существует jobtype с id= {} для удаления с помощью deleteJobTypeById", id);
            throw new HotelDataNotFoundException("There is no JobType for delete with this id =" + id);
        }

    }

    /**
     * Удаляет тип должности по названию (регистронезависимо).
     *
     * @param title название типа
     * @throws HotelDataNotFoundException если тип с указанным названием не найден
     */
    @Transactional
    public void deleteJobTypeByTitle(String title) {
        if (jobTypeRepository.deleteJobTypeByTitleIgnoreCase(title) == 0) {
            log.error("Error: не существует jobtype с title= {}  для удаления с помощью deleteJobTypeByTitle", title);
            throw new HotelDataNotFoundException("There is no JobType for delete with this title =" + title);
        }
    }

    /**
     * Обновляет описание и статус активности типа должности по названию.
     *
     * @param title       точное название типа (регистрозависимо)
     * @param description новое описание
     * @param status      строковое представление нового статуса ("true"/"false")
     * @throws HotelDataNotFoundException если обновление не затронуло ни одной записи
     */
    @Transactional
    public void updateJobTypesDescriptionAndActiveStatusByTitle(String title, String description, String status) {
        Boolean statusB = Boolean.valueOf(status);
        if (jobTypeRepository.updateJobTypesDescriptionAndActiveStatusByTitle(title, description, statusB) == 0) {
            throw new HotelDataNotFoundException("Problem with updating JobType");
        }
    }

    /**
     * Привязывает роль пользователя к типу должности.
     * <p>
     * Добавляет указанную роль в набор ролей, которые могут занимать данную должность.
     * </p>
     *
     * @param jobTypeTitle название типа должности
     * @param userTypeRole название роли пользователя
     * @return множество DTO ролей, привязанных к должности после обновления
     * @throws IndexOutOfBoundsException если тип должности не найден (используется {@code .get(0)})
     */
    @Transactional
    public Set<UserTypeDTO> addUserTypeToJobType(String jobTypeTitle, String userTypeRole) {

        jobTypeTitle = cleanString(jobTypeTitle);
        userTypeRole = cleanString(userTypeRole);

        List<JobType> list = jobTypeRepository.findJobTypesByTitleIgnoreCase(jobTypeTitle);
        if (list.isEmpty()) {
            throw new HotelDataNotFoundException("JobType not found: " + jobTypeTitle);
        }
        JobType jobType = list.get(0);

        Set<UserType> userTypeSet = jobType.getUserTypes();
        UserType userType = userTypeInternalService.findActiveUserTypeByType(userTypeRole);
        userTypeSet.add(userType);
        jobType = jobTypeRepository.save(jobType);
        return jobType.getUserTypes().stream().map(ut -> UserTypeMapper.INSTANCE.userTypeToUserTypeDTO(ut))
                .collect(Collectors.toSet());
    }

    /**
     * Очищает строку: удаляет пробелы по краям и приводит к нижнему регистру.
     *
     * @param line исходная строка
     * @return очищенная строка
     */
    private String cleanString(String line) {
        return line.trim().toLowerCase();
    }
    // TODO : переделать для админа и менеджера для изменений см. репорт сервисы
}
