package com.asv.models.userdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
/**
 * Упрощённый DTO пользователя, предназначенный для обмена данными с внешними сервисами.
 * <p>
 * Содержит минимальный набор идентифицирующей информации
 * </p>
 * <p>
 * Все поля проходят валидацию по формату, длине и запрету пустых значений.
 */
@Schema(description = "Упрощенная Модель данных пользователя для других сервисов с минимальным количеством " +
        "данных для поиск")
@Builder
@Data
public class UserExternalDTO {
    /**
     * Имя пользователя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы (например, "Анна").
     * Длина должна быть от 3 до 50 символов.
     */
    @Schema(description = "имя пользователя", example = "Максим")
    @NotBlank(message = "имя пользователя, не должен быть пустым")
    @Size(min = 3,max = 50,message = "количество символов 3-50")
    @Pattern(
            regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
            message = "Имя может содержать только русские буквы, дефисы"
    )
    private String firstName;

    /**
     * Фамилия пользователя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы.
     * Длина должна быть от 3 до 50 символов.
     */
    @Schema(description = "Фамилия  пользователя", example = "Бугульма")
    @NotBlank(message = "Фамилия пользователя, не должен быть пустым")
    @Size(min = 3,max = 50,message = "количество символов 3-50")
    @Pattern(
            regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
            message = "Фамилия может содержать только русские буквы, дефисы"
    )
    private String lastName;

    /**
     * Адрес электронной почты пользователя.
     * <p>
     * Должен соответствовать стандартному формату email и иметь длину от 3 до 50 символов.
     */
    @Schema(description = "Email  пользователя", example = "Николай")
    @Size(min = 3,max = 50,message = "количество символов 3-50")
    @Email(message = "Некорректный email. Пример: user@example.com")
    private String email;

    /**
     * Номер телефона пользователя в цифровом формате без разделителей.
     * <p>
     * Пример: {@code 89065554433}. Длина — от 3 до 20 цифр.
     */
    @Schema(description = "Телефон  пользователя", example = "8686643")
    @NotBlank(message = "Телефон пользователя, не должен быть пустым")
    @Size(min = 3,max = 20,message = "количество символов 3-20")
    @Pattern(
            regexp = "^\\d+$",
            message = "Некорректный номер. Пример: 89065554433"
    )
    private String phoneNumber;


}
