package com.asv.hotel.repositories;

import com.asv.hotel.entities.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link JobType} (типы должностей сотрудников).
 * <p>
 * Предоставляет методы для:
 * <ul>
 *   <li>поиска по ID, названию и статусу активности;</li>
 *   <li>удаления по ID или названию;</li>
 *   <li>обновления описания и статуса активности по названию.</li>
 * </ul>
 * </p>
 * <p>
 * Все кастомные запросы реализованы через native SQL с поддержкой регистронезависимого поиска (ILIKE).
 * </p>
 */
@Repository
public interface JobTypeRepository extends JpaRepository<JobType, Long> {

    /**
     * Возвращает список всех типов должностей.
     * <p>
     * ⚠️ Этот метод дублирует стандартный {@link JpaRepository#findAll()}, так как наследует его.
     * Рекомендуется использовать унаследованный метод напрямую.
     * </p>
     *
     * @return список всех записей в таблице {@code job_types}
     */
    List<JobType> findAll();

    /**
     * Находит тип должности по уникальному идентификатору.
     *
     * @param id идентификатор записи
     * @return {@link Optional} с сущностью или пустой, если не найдена
     */
    @Query(value = "SELECT * FROM job_types WHERE id = :id", nativeQuery = true)
    Optional<JobType> findJobTypeById(@Param("id") Long id);

    /**
     * Выполняет регистронезависимый поиск типов должностей по названию (частичное совпадение).
     * <p>
     * Использует оператор {@code ILIKE} PostgreSQL (или аналог в других СУБД).
     * </p>
     *
     * @param title часть или полное название должности
     * @return список совпадающих записей (может быть пустым)
     */
    @Query(value = "SELECT * FROM job_types WHERE title ILIKE :title", nativeQuery = true)
    List<JobType> findJobTypesByTitleIgnoreCase(@Param("title") String title);

    /**
     * Регистронезависимый поиск активных типов должностей по названию.
     * <p>
     * Возвращает только записи, у которых {@code is_active = true}.
     * </p>
     *
     * @param title часть или полное название должности
     * @return список активных совпадающих записей
     */
    @Query(value = "SELECT * FROM job_types WHERE title ILIKE :title AND is_active = true", nativeQuery = true)
    List<JobType> findActiveJobTypesByTitleIgnoreCase(@Param("title") String title);

    /**
     * Регистронезависимый поиск типов должностей по названию и статусу активности.
     * <p>
     * Не учитывает связь с типами пользователей (работает только с таблицей {@code job_types}).
     * </p>
     *
     * @param title    часть или полное название должности
     * @param isactive флаг активности: {@code true} — только активные, {@code false} — только неактивные
     * @return список записей, соответствующих критериям
     */
    @Query(value = "SELECT * FROM job_types WHERE title ILIKE :title AND is_active = :isactive", nativeQuery = true)
    List<JobType> findJobTypesByTitleAndActiveStatusWithoutUserType(@Param("title") String title,
                                                                    @Param("isactive") Boolean isactive);

    /**
     * Удаляет тип должности по идентификатору.
     *
     * @param id идентификатор записи
     * @return количество удалённых строк (обычно 0 или 1)
     */
    @Modifying
    @Query(value = "DELETE FROM job_types WHERE id = :id", nativeQuery = true)
    int deleteJobTypeById(@Param("id") Long id);

    /**
     * Удаляет все типы должностей, название которых совпадает (регистронезависимо) с заданным.
     *
     * @param title название для поиска и удаления
     * @return количество удалённых строк
     */
    @Modifying
    @Query(value = "DELETE FROM job_types WHERE title ILIKE :title", nativeQuery = true)
    int deleteJobTypeByTitleIgnoreCase(@Param("title") String title);

    /**
     * Обновляет описание и статус активности для всех записей с указанным названием.
     *
     * @param title       точное название типа должности (регистрозависимое)
     * @param description новое описание
     * @param isactive    новый статус активности
     * @return количество обновлённых строк
     */
    @Modifying
    @Query(value = "UPDATE job_types SET description = :description, is_active = :isactive WHERE title = :title", nativeQuery = true)
    int updateJobTypesDescriptionAndActiveStatusByTitle(@Param("title") String title,
                                                        @Param("description") String description,
                                                        @Param("isactive") Boolean isactive);


}
