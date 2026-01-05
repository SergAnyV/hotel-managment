package com.asv.hotel.repositories;

import com.asv.hotel.entities.User;
import com.asv.hotel.entities.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;
/**
 * Репозиторий для работы с пользователями системы ({@link User}).
 * <p>
 * Предоставляет методы для:
 * <ul>
 *   <li>поиска по ФИО, телефону, nickname и токену подтверждения;</li>
 *   <li>удаления по ФИО;</li>
 *   <li>обновления полных данных пользователя, включая смену роли;</li>
 *   <li>получения типа пользователя (роли) по nickname.</li>
 * </ul>
 * </p>
 * <p>
 * Все запросы реализованы через native SQL. Поиск по текстовым полям — регистронезависимый (ILIKE),
 * за исключением поиска по {@code nick_name} и {@code verification_token} (точное совпадение).
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по фамилии и имени (регистронезависимо, частичное совпадение).
     * Возвращает не более одной записи.
     *
     * @param lastName  фамилия (или её часть)
     * @param firstName имя (или его часть)
     * @return {@link Optional} с пользователем или пустой, если не найден
     */
    @Query(value = "SELECT * FROM users WHERE last_name ILIKE :last_name AND first_name ILIKE :first_name LIMIT 1", nativeQuery = true)
    Optional<User> findUserByLastNameAndFirstName(@Param("last_name") String lastName
            , @Param("first_name") String firstName);

    /**
     * Удаляет пользователей, у которых фамилия и имя совпадают (регистронезависимо, частично).
     * <p>
     * ⚠️ Может удалить несколько записей, если найдено более одного совпадения.
     * </p>
     *
     * @param lastName  фамилия (или её часть)
     * @param firstName имя (или его часть)
     * @return количество удалённых строк
     */
    @Modifying
    @Query(value = "DELETE FROM users WHERE last_name ILIKE :last_name AND first_name ILIKE :first_name", nativeQuery = true)
    int deleteUserByLastNameAndFirstName(@Param("last_name") String lastName
            , @Param("first_name") String firstName);

    /**
     * Находит пользователя по номеру телефона (регистронезависимо, частичное совпадение).
     *
     * @param phone номер телефона или его часть
     * @return {@link Optional} с пользователем или пустой, если не найден
     */
    @Query(value = "SELECT * FROM users WHERE phone ILIKE :phone", nativeQuery = true)
    Optional<User> findUserByPhoneNumber(@Param("phone") String phone);

    /**
     * Полностью обновляет данные пользователя, включая смену роли.
     * <p>
     * Роль определяется по названию (регистронезависимо) через подзапрос к таблице {@code user_types}.
     * </p>
     *
     * @param userId        идентификатор пользователя
     * @param nickName      новый nickname (уникальный логин)
     * @param firstName     новое имя
     * @param fathersName   новое отчество (может быть null)
     * @param lastName      новая фамилия
     * @param email         новый email
     * @param phoneNumber   новый телефон
     * @param password      новый хеш пароля
     * @param role          название роли (например: "CLIENT", "ADMIN") — регистронезависимо
     * @return количество обновлённых строк (обычно 1 при успехе)
     */
    @Modifying
    @Query(value = """
            UPDATE users SET nick_name = :nickName, first_name = :firstName, fathers_name = :fathersName, last_name = :lastName,
                email = :email, phone = :phoneNumber, password = :password, 
                    role_id = (SELECT id FROM user_types WHERE role ILIKE :role)
            WHERE id = :userId
            """, nativeQuery = true)
    int updateUser(
            @Param("userId") Long userId,
            @Param("nickName") String nickName,
            @Param("firstName") String firstName,
            @Param("fathersName") String fathersName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("password") String password,
            @Param("role") String role);

    /**
     * Находит пользователя по точному совпадению nickname (регистрозависимо).
     *
     * @param nickName уникальный логин пользователя
     * @return {@link Optional} с пользователем или пустой, если не найден
     */
    @Query(value = "SELECT * FROM users WHERE nick_name = :nickName", nativeQuery = true)
    Optional<User> findUserByNickName(@Param("nickName") String nickName);

    /**
     * Возвращает тип пользователя (роль), связанный с пользователем, имеющим указанный nickname.
     *
     * @param nickname nickname пользователя
     * @return {@link Optional} с ролью или пустой, если пользователь не найден
     */
    @Query(value = """
            SELECT * FROM user_types 
            WHERE id= ( SELECT role_id FROM users 
                        WHERE nick_name =:nickname )
            """, nativeQuery = true
    )
    Optional<UserType> findUserTypeByUserNickName(@Param("nickname") String nickname);

    /**
     * Находит пользователя по токену подтверждения регистрации.
     *
     * @param token уникальный токен верификации
     * @return {@link Optional} с пользователем или пустой, если не найден
     */
    @Query(value = "SELECT * FROM users WHERE verification_token = :token", nativeQuery = true)
    Optional<User> findUserByToken(@Param("token") String token);
}
