package com.asv.hotel.dto.userdto;


import com.asv.hotel.dto.bookingdto.BookingDTO;
import com.asv.hotel.dto.notificationdto.NotificationHotelDto;
import com.asv.hotel.entities.Report;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
/**
 * DTO (Data Transfer Object) для представления данных пользователя в системе бронирования отелей.
 * <p>
 * Используется для передачи информации о пользователе через REST API, включая его личные данные,
 * тип роли, а также связанные сущности: бронирования, отчёты и уведомления.
 * </p>
 * <p>
 * При сериализации:
 * <ul>
 *   <li>Поле {@code password} скрыто при чтении (только для записи),</li>
 *   <li>Коллекции ({@code bookingSet}, {@code reports}, {@code notificationHotels}) доступны только для чтения.</li>
 * </ul>
 * </p>
 * Все поля проходят строгую валидацию по длине, формату и запрету пустых значений.
 */
@Builder
@Data
@Schema(description = "Модель данных пользователя с описание его характеристик" +
        ", списко бронирований данного пользователя ,модель передачи данных через JSON")
public class UserDTO {
    /**
     * Уникальное отображаемое имя пользователя в системе.
     * <p>
     * Должно содержать от 3 до 20 символов.
     */
    @Schema(description = "никнайм пользователя", example = "Goldman")
    @NotBlank(message = "никнайм пользователя, не должен быть пустым")
    @Size(min = 3, max = 20, message = "количество символов 3-20")
    private String nickName;
    /**
     * Имя пользователя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы (например, "Мария").
     * Длина — от 3 до 50 символов.
     */
    @Schema(description = "имя пользователя", example = "Николай")
    @NotBlank(message = "имя пользователя, не должен быть пустым")
    @Size(min = 3, max = 50, message = "количество символов 3-50")
    @Pattern(
            regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
            message = "Имя может содержать только русские буквы, дефисы"
    )
    private String firstName;
    /**
     * Отчество пользователя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы.
     * Длина — от 3 до 50 символов.
     */
    @Schema(description = "Отчество пользователя", example = "Иванович")
    @NotBlank(message = "Отчество пользователя, не должен быть пустым")
    @Size(min = 3, max = 50, message = "количество символов 3-50")
    @Pattern(
            regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
            message = "Отчество может содержать только русские буквы, дефисы"
    )
    private String fathersName;
    /**
     * Фамилия пользователя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы.
     * Длина — от 3 до 50 символов.
     */
    @Schema(description = "Фамилия  пользователя", example = "Бугульма")
    @NotBlank(message = "Фамилия пользователя, не должен быть пустым")
    @Size(min = 2, max = 50, message = "количество символов 3-50")
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
    @Schema(description = "Маил  пользователя", example = "Bugul@Nikol.con")
    @Size(min = 3, max = 50, message = "количество символов 3-50")
    @Email(message = "Некорректный email. Пример: user@example.com")
    private String email;
    /**
     * Номер телефона пользователя в цифровом формате без разделителей.
     * <p>
     * Пример: {@code 89065554433}. Длина — от 3 до 20 цифр.
     */
    @Schema(description = "Телефон  пользователя", example = "89876543322")
    @NotBlank(message = "Телефон пользователя, не должен быть пустым")
    @Size(min = 3, max = 20, message = "количество символов 3-20")
    @Pattern(
            regexp = "^\\d+$",
            message = "Некорректный номер. Пример: 89065554433"
    )
    private String phoneNumber;
    /**
     * Пароль пользователя.
     * <p>
     * При сериализации в JSON скрывается (не возвращается клиенту).
     * Длина — от 3 до 120 символов.
     */
    @Schema(description = "Пароль  пользователя", example = "123456789")
    @NotBlank(message = "Пароль пользователя, не должен быть пустым")
    @Size(min = 3, max = 120, message = "количество символов 3-120")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    /**
     * Тип (роль) пользователя в системе (например, "клиент", "уборщик", "повар").
     * <p>
     * Длина — от 3 до 20 символов.
     */
    @Schema(description = "Тип  пользователя", example = "повар")
    @Size(min = 3, max = 20, message = "количество символов 3-20")
    private String type;
    /**
     * Список бронирований, созданных данным пользователем.
     * <p>
     * Доступен только для чтения (не учитывается при десериализации входящих запросов).
     */
    @Schema(description = "Бронирования пользователя", accessMode = Schema.AccessMode.READ_ONLY)
    @EqualsAndHashCode.Exclude
    private Set<BookingDTO> bookingSet;

    /**
     * Отчёты, связанные с пользователем (например жалобы и т.д.).
     * <p>
     * Доступны только для чтения.
     */
    @Schema(description = "Отчеты пользователя", accessMode = Schema.AccessMode.READ_ONLY)
    @EqualsAndHashCode.Exclude
    private Set<Report> reports;
    /**
     * Уведомления, отправленные или предназначенные данному пользователю.
     * <p>
     * Доступны только для чтения.
     */
    @Schema(description = "Сообщения пользователя", accessMode = Schema.AccessMode.READ_ONLY)
    @EqualsAndHashCode.Exclude
    private Set<NotificationHotelDto> notificationHotels;

}
