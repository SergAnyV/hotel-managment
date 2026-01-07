package com.asv.hotel.services;

import com.asv.hotel.dto.usertypedto.UserTypeDTO;
import com.asv.hotel.entities.UserType;

import java.util.List;

/**
 * Сервис для управления типами пользователей (ролями) в системе отеля.
 * <p>
 * Обеспечивает создание, поиск, обновление и удаление ролей (например: CLIENT, ADMIN, STAFF),
 * а также проверку их активности.
 * </p>
 */
public interface UserTypeService {

    /**
     * Создаёт новый тип пользователя (роль).
     *
     * @param userTypeDTO данные новой роли
     * @return DTO созданной роли
     */
    UserTypeDTO createUserType(UserTypeDTO userTypeDTO);

    /**
     * Возвращает список всех типов пользователей (ролей).
     *
     * @return список DTO всех ролей
     */
    List<UserTypeDTO> findAllUserTypeDTOs();

    /**
     * Удаляет роль по названию (регистронезависимо).
     *
     * @param role название роли (например: "CLIENT", "ADMIN")
     */
    void deleteUserTypeByType(String role);

    /**
     * Удаляет все типы пользователей из системы.
     * <p>
     * ⚠️ Опасная операция — используйте с осторожностью.
     * </p>
     */
    void deleteAllUserTypes();

    /**
     * Находит роль по названию и возвращает её в виде DTO (регистронезависимо).
     *
     * @param role название роли
     * @return DTO роли или {@code null}, если не найдена
     */
    UserTypeDTO findUserTypeDTOByType(String role);

    /**
     * Находит роль по названию и возвращает сущность (регистронезависимо).
     *
     * @param role название роли
     * @return сущность {@link UserType} или {@code null}, если не найдена
     */
    UserType findUserTypeByType(String role);

    /**
     * Обновляет данные существующей роли.
     *
     * @param userTypeDTO обновлённые данные роли
     * @return DTO обновлённой роли
     */
    UserTypeDTO cahngeDataUserType(UserTypeDTO userTypeDTO);

    /**
     * Находит активную роль по названию.
     * <p>
     * Возвращает роль только если она существует и её статус {@code isActive = true}.
     * </p>
     *
     * @param role название роли
     * @return сущность {@link UserType}, если роль активна
     * @throws com.asv.hotel.exceptions.HotelDataNotFoundException если роль не найдена или неактивна
     */
    UserType findActiveUserTypeByType(String role);

}
