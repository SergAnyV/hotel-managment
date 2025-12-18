package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.usertypedto.UserTypeDTO;
import com.asv.hotel.dto.usertypedto.UserTypeSimpleDTO;
import com.asv.hotel.entities.UserType;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserTypeMapper {

    /**
     * Экземпляр маппера, созданный через фабрику MapStruct.
     * <p>
     * Предоставляет доступ к сгенерированным методам без необходимости внедрения через Spring.
     * </p>
     */
    UserTypeMapper INSTANCE = Mappers.getMapper(UserTypeMapper.class);

    /**
     * Преобразует сущность {@link UserType} в полный DTO {@link UserTypeDTO}.
     *
     * @param userType сущность типа пользователя из базы данных
     * @return полный DTO с описанием типа пользователя
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "jobTypeList", source = "jobTypeList")
    UserTypeDTO userTypeToUserTypeDTO(UserType userType);

    /**
     * Преобразует полный DTO {@link UserTypeDTO} обратно в сущность {@link UserType}.
     *
     * @param userTypeDTO входящий DTO с полными данными
     * @return сущность для сохранения или обновления в БД
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "jobTypeList", source = "jobTypeList")
    UserType UserTypeDTOToUserType(UserTypeDTO userTypeDTO);

    /**
     * Преобразует сущность {@link UserType} в упрощённый DTO {@link UserTypeSimpleDTO}.
     *
     * @param userType сущность типа пользователя
     * @return упрощённый DTO
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    UserTypeSimpleDTO UserTypeToUserTypeSimpleDTO(UserType userType);

    /**
     * Преобразует полный DTO {@link UserTypeDTO} в упрощённую версию {@link UserTypeSimpleDTO}.
     *
     * @param userTypeDTO полный DTO
     * @return упрощённый DTO (только название и описание)
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    UserTypeSimpleDTO UserTypeDTOToUserTypeSimpleDTO(UserTypeDTO userTypeDTO);

    /**
     * Частично обновляет существующую сущность {@link UserType} на основе данных из {@link UserTypeDTO}.
     * <p>
     * Особенности:
     * <ul>
     *   <li>Только ненулевые/непустые поля из DTO обновляют соответствующие поля сущности;</li>
     *   <li>Поля со значением {@code null} в DTO игнорируются и не сбрасываются в сущности;</li>
     * </ul>
     * </p>
     *
     * @param userTypeDTO DTO с частично обновлёнными данными
     * @param userType    существующая сущность, подлежащая обновлению
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateuserTypeFromuserTypeDTO(UserTypeDTO userTypeDTO, @MappingTarget UserType userType);


}
