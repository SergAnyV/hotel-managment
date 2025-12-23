package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.jobtypedto.JobTypeDTO;
import com.asv.hotel.dto.jobtypedto.JobTypeSimpleDTO;
import com.asv.hotel.entities.JobType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
/**
 * Mapper для преобразования между сущностью {@link JobType} и её DTO-представлениями.
 * <p>
 * Использует библиотеку MapStruct для генерации эффективного кода конвертации
 * между доменной моделью и объектами передачи данных (DTO).
 * </p>
 * <p>
 * Подключает вспомогательный маппер {@link UserTypeMapper} для корректного преобразования
 * связанных типов пользователей ({@code userTypes}).
 * </p>
 * <p>
 * Поддерживает преобразования между:
 * <ul>
 *   <li>Полной сущностью {@link JobType} ↔ {@link JobTypeDTO} (с активностью и ролями);</li>
 *   <li>Полной и упрощённой DTO-моделями ({@link JobTypeDTO} ↔ {@link JobTypeSimpleDTO});</li>
 *   <li>Сущностью и упрощённой DTO ({@link JobType} ↔ {@link JobTypeSimpleDTO}).</li>
 * </ul>
 * </p>
 */
@Mapper(uses = UserTypeMapper.class)
public interface JobTypeMapper {
    /**
     * Экземпляр маппера, созданный через фабрику MapStruct.
     * <p>
     * Предоставляет доступ к сгенерированным методам без необходимости внедрения через Spring.
     * </p>
     */
    JobTypeMapper INSTANCE = Mappers.getMapper(JobTypeMapper.class);

    /**
     * Преобразует сущность {@link JobType} в полный DTO {@link JobTypeDTO}.
     * <p>
     * Копирует все поля, включая статус активности и связанные типы пользователей.
     * Для преобразования {@code userTypes} используется {@link UserTypeMapper}.
     * </p>
     *
     * @param jobType сущность типа работы
     * @return полный DTO с описанием типа работы
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "userTypes", source = "userTypes")
    JobTypeDTO jobTypeToJobTypeDTO(JobType jobType);

    /**
     * Преобразует полный DTO {@link JobTypeDTO} обратно в сущность {@link JobType}.
     * <p>
     * Используется, например, при создании или обновлении типа работы.
     * Поле {@code userTypes} преобразуется с помощью {@link UserTypeMapper}.
     * </p>
     *
     * @param jobTypeDTO входящий DTO с полными данными
     * @return сущность для сохранения в БД
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "userTypes", source = "userTypes")
    JobType jobTypeDTOToJobtype(JobTypeDTO jobTypeDTO);

    /**
     * Преобразует полный DTO {@link JobTypeDTO} в упрощённую версию {@link JobTypeSimpleDTO}.
     * <p>
     * Используется в сценариях, где не требуется информация о статусе активности или ролях,
     * например, при отображении справочника работ.
     * </p>
     *
     * @param jobTypeDTO полный DTO
     * @return упрощённый DTO (только название и описание)
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    JobTypeSimpleDTO jobTypeDTOToJobTypeSimpleDTO(JobTypeDTO jobTypeDTO);

    /**
     * Преобразует упрощённый DTO {@link JobTypeSimpleDTO} в полный DTO {@link JobTypeDTO}.
     * <p>
     * Поля {@code isActive} и {@code userTypes} будут иметь значения по умолчанию
     *, если не заданы явно.
     * Может использоваться для подготовки данных перед сохранением.
     * </p>
     *
     * @param jobTypeSimpleDTO упрощённый DTO
     * @return полный DTO (частично заполненный)
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    JobTypeDTO jobTypeSimpleDTOToJobTypeDTO(JobTypeSimpleDTO jobTypeDTO);

    /**
     * Преобразует сущность {@link JobType} напрямую в упрощённый DTO {@link JobTypeSimpleDTO}.
     * @param jobType сущность типа работы
     * @return упрощённый DTO
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    JobTypeSimpleDTO jobTypeToJobTypeSimpleDTO(JobType jobType);

    /**
     * Преобразует упрощённый DTO {@link JobTypeSimpleDTO} в сущность {@link JobType}.
     * <p>
     * Поля {@code isActive} и {@code userTypes} будут инициализированы значениями по умолчанию.
     * Требует последующей настройки этих полей в сервисном слое при необходимости.
     * </p>
     *
     * @param jobTypeSimpleDTO упрощённый DTO от клиента
     * @return сущность (частично заполненная)
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    JobType jobTypeSimpleDTOToJobType(JobTypeSimpleDTO jobTypeSimpleDTO);
}
