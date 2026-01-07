package com.asv.hotel.services;

import com.asv.hotel.dto.userdto.UserDTO;

/**
 * Сервис для управления пользователями системы (клиентами и сотрудниками отеля).
 * <p>
 * Предоставляет методы для регистрации, поиска, обновления и удаления пользователей,
 * а также для подтверждения регистрации по email.
 * </p>
 */
public interface UserService {

    /**
     * Регистрирует нового пользователя и инициирует отправку email для подтверждения.
     *
     * @param userDTO данные нового пользователя
     * @return DTO созданного пользователя (со статусом "не подтверждён")
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * Находит пользователя по фамилии и имени (регистронезависимо, частичное совпадение).
     *
     * @param lastName  фамилия или её часть
     * @param firstName имя или его часть
     * @return DTO пользователя или {@code null}, если не найден
     */
    UserDTO findUserDTOByLastNameAndFirstName(String lastName, String firstName);

    /**
     * Удаляет пользователя по фамилии и имени (регистронезависимо, частичное совпадение).
     *
     * @param lastName  фамилия или её часть
     * @param firstName имя или его часть
     */
    void deleteUserByLastNameAndFirstName(String lastName, String firstName);

    /**
     * Находит пользователя по номеру телефона (регистронезависимо, частичное совпадение).
     *
     * @param phoneNumber номер телефона или его часть
     * @return DTO пользователя или {@code null}, если не найден
     */
    UserDTO findUserDTOByPhoneNumber(String phoneNumber);

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param userDTO обновлённые данные пользователя
     * @return DTO обновлённого пользователя
     */
    UserDTO changeDataUser(UserDTO userDTO);

    /**
     * Подтверждает регистрацию пользователя по токену верификации.
     *
     * @param token токен из email-ссылки
     * @return {@code true}, если подтверждение успешно; {@code false}, если токен недействителен
     */
    Boolean confirmRegistrationUser(String token);

}
