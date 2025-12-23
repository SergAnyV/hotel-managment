package com.asv.models.userdto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Упрощённый DTO пользователя для внутреннего использования в системе.
 * <p>
 * Содержит базовую информацию о пользователе: учётные данные (никнейм), ФИО,
 * контактные данные (email и телефон).
 * </p>
 * <p>
 * Все строковые поля проходят валидацию по длине, формату и запрету пустых значений.
 */
@Schema(description = "Упрощенная Модель данных пользователя с описание его характеристик" +
        "модель передачи данных через JSON")
@Builder
@Data
public class UserSimpleDTO {
    /**
     * Уникальное отображаемое имя пользователя в системе.
     * <p>
     * Должно содержать от 3 до 20 символов (латиница, цифры, символы подчёркивания допустимы).
     */
    @Schema(description = "никнайм пользователя", example = "cleaner_maria")
    @NotBlank(message = "никнайм пользователя, не должен быть пустым")
    @Size(min = 3, max = 20, message = "количество символов 3-20")
    private String nickName;

    @Schema(description = "имя пользователя", example = "Мария")
    @NotBlank(message = "имя пользователя, не должен быть пустым")
    @Size(min = 3, max = 50, message = "количество символов 3-50")
    @Pattern(
            regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
            message = "Имя может содержать только русские буквы, дефисы"
    )
    private String firstName;
    /**
     * Имя пользователя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы (например, "Мария-Анна").
     * Длина — от 3 до 50 символов.
     */
    @Schema(description = "Отчество пользователя", example = "Александровна")
    @NotBlank(message = "Отчество пользователя, не должен быть пустым")
    @Size(min = 3, max = 50, message = "количество символов 3-50")
    @Pattern(
            regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
            message = "Отчество может содержать только русские буквы, дефисы"
    )
    private String fathersName;
    /**
     * Отчество пользователя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы.
     * Длина — от 3 до 50 символов.
     */
    @Schema(description = "Фамилия  пользователя", example = "Смирнова")
    @NotBlank(message = "Фамилия пользователя, не должен быть пустым")
    @Size(min = 3, max = 50, message = "количество символов 3-50")
    @Pattern(
            regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
            message = "Фамилия может содержать только русские буквы, дефисы"
    )
    private String lastName;
    /**
     * Фамилия пользователя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы.
     * Длина — от 3 до 50 символов.
     */
    @Schema(description = "email  пользователя", example = "cleaner@hotel.ru")
    @Size(min = 3, max = 50, message = "количество символов 3-50")
    @Email(message = "Некорректный email. Пример: user@example.com")
    private String email;
    /**
     * Адрес электронной почты пользователя.
     * <p>
     * Должен соответствовать стандартному формату email и иметь длину от 3 до 50 символов.
     */
    @Schema(description = "Телефон  пользователя только в числовом формате без '+' ", example = "79164567890")
    @NotBlank(message = "Телефон пользователя, не должен быть пустым")
    @Size(min = 3, max = 20, message = "количество символов 3-20")
    @Pattern(
            regexp = "^\\d+$",
            message = "Некорректный номер. Пример: 89065554433"
    )
    private String phoneNumber;


}
