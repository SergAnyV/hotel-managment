package com.asv.hotel.services;

import com.asv.hotel.entities.User;
import com.asv.hotel.entities.UserType;

/**
 * Расширенный интерфейс сервиса управления пользователями.
 * <p>
 * Предназначен для внутреннего использования в слое сервисов (а не в контроллерах).
 * Добавляет методы, возвращающие сущности {@link User} и {@link UserType} напрямую,
 * в отличие от публичного интерфейса {@link UserService}, оперирующего только DTO.
 * </p>
 */
public interface UserInternalService extends UserService {

    /**
     * Находит пользователя по фамилии и имени (регистронезависимо, частичное совпадение).
     *
     * @param lastName  фамилия или её часть
     * @param firstName имя или его часть
     * @return сущность {@link User} или {@code null}, если не найдена
     */
    User findUserByLastNameAndFirstName(String lastName, String firstName);

    /**
     * Находит пользователя по уникальному nickname (логину).
     *
     * @param nickName уникальный псевдоним пользователя
     * @return сущность {@link User} или {@code null}, если не найдена
     */
    User findUserByNickName(String nickName);

    /**
     * Возвращает тип пользователя (роль), связанный с пользователем по его nickname.
     *
     * @param nickName nickname пользователя
     * @return сущность {@link UserType} или {@code null}, если не найдена
     */
    UserType findUserTypeByUserNickName(String nickName);
}
