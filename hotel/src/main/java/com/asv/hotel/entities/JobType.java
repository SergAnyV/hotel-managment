package com.asv.hotel.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность типа должности (профессии) сотрудника в отеле.
 * <p>
 * Описывает возможную роль или специализацию персонала (например: "Администратор", "Уборщик", "Повар").
 * </p>
 * <p>
 * Каждый тип должности:
 * <ul>
 *   <li>имеет уникальное название;</li>
 *   <li>может быть активным или неактивным;</li>
 *   <li>связан с одним или несколькими типами пользователей (ролями), которые могут её занимать.</li>
 * </ul>
 * </p>
 * <p>
 * Поддерживает автоматическое управление временными метками создания и обновления.
 * </p>
 */
@Entity
@Table(name = "job_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobType {


    /**
     * Уникальный идентификатор типа должности.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название должности (например: "Администратор").
     * Обязательное, уникальное, до 50 символов.
     */
    @Column(name = "title", nullable = false, unique = true, length = 50)
    private String title;

    /**
     * Описание обязанностей или назначения должности.
     * Обязательное поле, до 250 символов.
     */
    @Column(name = "description", nullable = false, length = 250)
    private String description;

    /**
     * Флаг активности: указывает, может ли эта должность использоваться в системе.
     * {@code true} — активна, {@code false} — неактивна (архивная).
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * Дата и время создания записи. Устанавливается автоматически при первом сохранении.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления записи. Обновляется автоматически при каждом изменении.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Набор типов пользователей (ролей), которым разрешено занимать эту должность.
     * Связь многие-ко-многим через таблицу {@code user_type_job_type}.
     * Загружается лениво (LAZY).
     * Инициализируется пустым {@link HashSet} для предотвращения {@code NullPointerException}.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_type_job_type",
            joinColumns = @JoinColumn(name = "job_type_id"),
            inverseJoinColumns = @JoinColumn(name = "user_type_id")
    )
    private Set<UserType> userTypes = new HashSet<>();

}
