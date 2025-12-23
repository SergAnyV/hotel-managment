package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.userdto.UserDTO;
import com.asv.hotel.dto.userdto.UserSimpleDTO;
import com.asv.hotel.entities.User;
import com.asv.hotel.services.UserTypeService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Mapper для преобразования между сущностью {@link User} и её DTO-представлениями.
 * <p>
 * Использует библиотеку MapStruct для генерации эффективного кода конвертации
 * между доменной моделью пользователя и объектами передачи данных (DTO).
 * </p>
 * <p>
 * Особенности:
 * <ul>
 *   <li>Поддерживает шифрование пароля при создании/обновлении пользователя;</li>
 *   <li>Игнорирует служебные коллекции (бронирования, отчёты) для предотвращения избыточной загрузки данных;</li>
 *   <li>Интегрируется с вспомогательными мапперами для связанных сущностей;</li>
  * </ul>
 * </p>
 * <p>
 * Подключает следующие вспомогательные мапперы:
 * <ul>
 *   <li>{@link UserTypeMapper} — для преобразования типов пользователей;</li>
 *   <li>{@link BookingMapper} — для вложенных данных бронирований (в теории, хотя в текущих методах не используется);</li>
 *   <li>{@link NotificationMapper} — для уведомлений (аналогично).</li>
 * </ul>
 * </p>
 */
@Mapper(uses = {UserTypeMapper.class, BookingMapper.class, NotificationMapper.class})
public interface UserMapper {
    /**
     * Экземпляр маппера, созданный через фабрику MapStruct.
     * <p>
     * Предоставляет доступ к сгенерированным методам без необходимости внедрения через Spring.
     * </p>
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Преобразует сущность {@link User} в полный DTO {@link UserDTO}.
     * <p>
     * Особенности:
     * <ul>
     *   <li>Тип пользователя извлекается как строковое имя ({@code type.name});</li>
     *   <li>Коллекции {@code bookingSet} и {@code reports} игнорируются для избежания избыточной сериализации.</li>
     * </ul>
     * </p>
     *
     * @param user сущность пользователя из базы данных
     * @return DTO с основными данными пользователя
     */
    @Mapping(target = "type", source = "type.name")
    @Mapping(target = "bookingSet", ignore = true)
    @Mapping(target = "reports", ignore = true)
    UserDTO userToUserDTO(User user);

    /**
     * Преобразует полный DTO {@link UserDTO} в сущность {@link User}.
     * <p>
     * Особенности:
     * <ul>
     *   <li>Пароль шифруется с помощью BCrypt (см. {@link #encodePassword});</li>
     *   <li>Поля {@code id}, {@code createdAt}, {@code updatedAt}, {@code type}, {@code bookingSet}, {@code reports}
     *       игнорируются — они устанавливаются отдельно или генерируются системой;</li>
     *   <li>Предназначен для создания нового пользователя.</li>
     * </ul>
     * </p>
     *
     * @param userDTO входящий DTO с данными нового пользователя
     * @return сущность для сохранения в БД (с зашифрованным паролем)
     */
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "bookingSet", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    User userDTOToUser(UserDTO userDTO);

    /**
     * Преобразует сущность {@link User} в упрощённый DTO {@link UserSimpleDTO}.
     * <p>
     * Копирует только основные персональные данные: ФИО, никнейм, email и телефон.
     * Используется, например, при отображении информации о пользователе в составе других сущностей
     * (например, в бронировании).
     * </p>
     *
     * @param user сущность пользователя
     * @return упрощённый DTO с персональными данными
     */
    @Mapping(target = "nickName", source = "nickName")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "fathersName", source = "fathersName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    UserSimpleDTO userToUserSimpleDTO(User user);

    /**
     * Преобразует упрощённый DTO {@link UserSimpleDTO} в полный DTO {@link UserDTO}.
     * <p>
     * Поля {@code password}, {@code type}, {@code bookingSet}, {@code reports} остаются неинициализированными.
     * Может использоваться как промежуточный шаг перед дополнением данными в сервисе.
     * </p>
     *
     * @param userSimpleDTO упрощённый DTO
     * @return частично заполненный полный DTO
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "bookingSet", ignore = true)
    @Mapping(target = "reports", ignore = true)
    UserDTO userSimpleDTOToUseDTO(UserSimpleDTO userSimpleDTO);

    /**
     * Частично обновляет существующую сущность {@link User} на основе данных из {@link UserDTO}.
     * <p>
     * Особенности:
     * <ul>
     *   <li>Только ненулевые поля обновляются ({@code NullValuePropertyMappingStrategy.IGNORE});</li>
     * </ul>
     * </p>
     * <p>
     * Не вызывается напрямую — используется внутри {@link #updateUserFromDto}.
     * </p>
     *
     * @param dto DTO с обновлёнными данными
     * @param user существующая сущность для обновления
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "type", ignore = true) // Обрабатывается отдельно в updateUserFromDto
    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    void updateUserFieldsFromDto(UserDTO dto, @MappingTarget User user);

    default void updateUserFromDto(UserDTO dto, @MappingTarget User user, UserTypeService userTypeService) {
        updateUserFieldsFromDto(dto, user);
        // роль отдельно обрабатывается
        if (dto.getType() != null) {
            user.setType(userTypeService.findActiveUserTypeByType(dto.getType()));
        }
    }
    @Named("encodePassword")
    default String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            return null;
        }
        return getPasswordEncoder().encode(rawPassword);
    }

   // создание нового PasswordEncoder
    default PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }


}
