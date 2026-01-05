package com.asv.hotel.repositories;

import com.asv.hotel.entities.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления типами пользователей (ролями) — {@link UserType}.
 * <p>
 * Предоставляет методы для:
 * <ul>
 *   <li>поиска роли по названию;</li>
 *   <li>удаления по названию;</li>
 *   <li>обновления данных роли по ID;</li>
 *   <li>получения ролей, связанных с определённым типом должности (JobType).</li>
 * </ul>
 * </p>
 * <p>
 * Поиск и удаление по названию выполняются регистронезависимо с поддержкой частичного совпадения (ILIKE).
 * </p>
 */
@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Long> {

    /**
     * Находит тип пользователя по частичному или полному совпадению названия (регистронезависимо).
     * <p>
     * ⚠️ Поскольку поле {@code title} (или {@code name}) в сущности объявлено как {@code unique},
     * ожидается не более одного результата. Однако при частичном совпадении возможны множественные совпадения,
     * но метод возвращает только первый (или пустой результат, если совпадений нет).
     * </p>
     *
     * @param name часть или полное название роли (например: "админ", "client")
     * @return {@link Optional} с найденной ролью или пустой, если не найдена
     */
    @Query(value = "SELECT * FROM user_types WHERE name ILIKE :name", nativeQuery = true)
    Optional<UserType> findUserTypeByNameLikeIgnoreCase(@Param("name") String name);

    /**
     * Удаляет типы пользователей, название которых частично или полностью совпадает с указанным (регистронезависимо).
     * <p>
     * ⚠️ Может удалить несколько записей, если найдено более одного совпадения.
     * Обычно не рекомендуется для сущностей с уникальным названием.
     * </p>
     *
     * @param name часть или полное название роли
     * @return количество удалённых строк
     */
    @Modifying
    @Query(value = "DELETE FROM user_types WHERE name ILIKE :name", nativeQuery = true)
    int deleteByName(@Param("name") String name);

    /**
     * Обновляет данные типа пользователя по его идентификатору.
     *
     * @param id          идентификатор роли
     * @param name        новое название
     * @param description новое описание
     * @param isActive    новый статус активности
     * @return количество обновлённых строк (обычно 1 при успехе)
     */
    @Modifying
    @Query(value = """
            UPDATE user_types SET name = :name, description = :description, is_active = :isActive
            WHERE id = :id
            """, nativeQuery = true)
    int updateUserType(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("description") String description,
            @Param("isActive") boolean isActive);

    /**
     * Возвращает все типы пользователей (роли), которые могут занимать должность с указанным ID.
     * <p>
     * Связь определяется через таблицу {@code user_type_job_type}.
     * </p>
     *
     * @param jobTypeId идентификатор типа должности (JobType)
     * @return список ролей, связанных с указанной должностью (может быть пустым)
     */
    @Query(value = """
            SELECT ut.* 
            FROM user_types ut
            INNER JOIN user_type_job_type utjt ON ut.id = utjt.user_type_id
            WHERE utjt.job_type_id = :jobTypeId
            """, nativeQuery = true)
    List<UserType> findUserTypesByJobTypeId(@Param("jobTypeId") Long jobTypeId);
}
