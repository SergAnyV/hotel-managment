package com.asv.hotel.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность пользователя системы (клиент или сотрудник отеля).
 * <p>
 * Реализует интерфейс {@link UserDetails} для интеграции с Spring Security.
 * Поддерживает:
 * <ul>
 *   <li>аутентификацию по nickname и паролю;</li>
 *   <li>ролевой доступ через связь с {@link UserType};</li>
 *   <li>подтверждение регистрации через токен;</li>
 *   <li>связи с бронированиями, отчётами и уведомлениями.</li>
 * </ul>
 * </p>
 * <p>
 * Имеет уникальные ограничения на поля: nickname, email, телефон и токен подтверждения.
 * </p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class User implements UserDetails {

    /**
     * Уникальный идентификатор пользователя.
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный псевдоним (логин) пользователя. Используется как {@code username} в Spring Security.
     * Обязательное поле, до 30 символов.
     */
    @Column(name = "nick_name", unique = true, nullable = false, length = 30)
    private String nickName;

    /**
     * Имя пользователя. Обязательное поле, до 50 символов.
     */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * Отчество пользователя. Необязательное поле, до 50 символов.
     */
    @Column(name = "fathers_name", nullable = true, length = 50)
    private String fathersName;

    /**
     * Фамилия пользователя. Обязательное поле, до 50 символов.
     */
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * Адрес электронной почты. Должен быть уникальным. Необязательное поле (может быть null).
     */
    @Column(name = "email", length = 50, unique = true)
    private String email;

    /**
     * Номер телефона. Обязательное и уникальное поле, до 30 символов.
     */
    @Column(name = "phone", length = 30, unique = true, nullable = false)
    private String phoneNumber;

    /**
     * Хеш пароля (в формате, поддерживаемом Spring Security, например bcrypt).
     * Обязательное поле, до 120 символов.
     */
    @Column(name = "password", nullable = false, length = 120)
    private String password;

    /**
     * Дата и время регистрации пользователя.
     * Устанавливается автоматически при первом сохранении и не изменяется.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления данных пользователя.
     * Обновляется автоматически при каждом изменении.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Тип пользователя (роль): определяет права доступа (например: CLIENT, ADMIN, STAFF).
     * Обязательная связь. Исключён из {@code equals/hashCode} и {@code toString} для предотвращения рекурсии.
     */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private UserType type;

    /**
     * Набор бронирований, созданных этим пользователем.
     * Каскадное удаление "сирот" включено. Загружается лениво.
     * Исключён из {@code equals/hashCode} и {@code toString}.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Booking> bookingSet = new HashSet<>();

    /**
     * Набор отчётов, созданных этим пользователем (если он сотрудник).
     * Каскадное удаление "сирот" включено. Загружается лениво.
     * Исключён из {@code equals/hashCode} и {@code toString}.
     */
    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Report> reports = new HashSet<>();

    /**
     * Уведомления, адресованные этому пользователю.
     * Каскадное удаление включено. Загружается лениво.
     * Исключён из {@code equals/hashCode} и {@code toString}.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<NotificationHotel> notificationHotels = new HashSet<>();

    /**
     * Токен для подтверждения регистрации по электронной почте.
     * Уникальный, обязательный, до 50 символов.
     * Исключён из {@code equals/hashCode} и {@code toString} по соображениям безопасности.
     */
    @Column(name = "verification_token", nullable = false, unique = true, length = 50)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private String verificationToken;

    /**
     * Статус подтверждения регистрации: {@code true} — подтверждён, {@code false} — ожидает подтверждения.
     * Обязательное поле.
     * Исключён из {@code equals/hashCode} и {@code toString} по соображениям безопасности.
     */
    @Column(name = "verify_status", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Boolean verifyStatus;

    // === Реализация UserDetails ===

    /**
     * Возвращает полномочия (роли) пользователя для Spring Security.
     * Формат: "ROLE_{название_роли}", например: "ROLE_CLIENT".
     *
     * @return коллекция с одним элементом — ролью пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.type.getRole().name()));
    }

    /**
     * Возвращает уникальное имя пользователя (логин) — используется для аутентификации.
     *
     * @return nickname пользователя
     */
    @Override
    public String getUsername() {
        return this.nickName;
    }

    /**
     * Возвращает хеш пароля.
     *
     * @return зашифрованный пароль
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Указывает, не истёк ли срок действия учётной записи.
     * В текущей реализации — всегда {@code true}.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, не заблокирована ли учётная запись.
     * В текущей реализации — всегда {@code true}.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, не истёк ли срок действия учётных данных (пароля).
     * В текущей реализации — всегда {@code true}.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, включён ли пользователь (активен ли для входа).
     * В текущей реализации — всегда {@code true}.
     * ⚠️ Для поддержки деактивации используйте отдельное поле (например, {@code isActive}).
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
