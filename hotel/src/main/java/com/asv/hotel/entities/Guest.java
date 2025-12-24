package com.asv.hotel.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

/**
 * Представление данных гостя в системе бронирования отелей.
 * <p>
 * Используется для передачи информации о госте, включая его личные данные.
 * </p>
 * Все поля проходят строгую валидацию по длине, формату и запрету пустых значений.
 */
@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель данных гостя с описание его основных характеристик")
public class Guest implements Serializable {
    /**
     * Имя гостя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы (например, "Мария").
     * Длина — от 3 до 50 символов.
     */
    @Schema(description = "имя гостя", example = "Николай")
    @Size(min = 3, max = 50, message = "количество символов 3-50")
    @Pattern(
            regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
            message = "Имя может содержать только русские буквы, дефисы"
    )
    private String name;
    /**
     * Фамилия гостя на русском языке.
     * <p>
     * Допускаются только русские буквы и дефисы.
     * Длина — от 3 до 50 символов.
     */
    @Schema(description = "Фамилия  гостя", example = "Бугульма")
    @Size(min = 3, max = 50, message = "количество символов 3-50")
    @Pattern(
            regexp = "^[А-ЯЁа-яё]+(?:-[А-ЯЁа-яё]+)*$",
            message = "Фамилия может содержать только русские буквы, дефисы"
    )
    private String surname;
    /**
     * Возраст гостя в цифровом формате.
     * <p>
     * Допускается только положительные значения от 0 до 127.
     */
    @Schema(description = "Возраст гостя", example = "20")
    @Min(0)
    @Max(127)
    private Byte age;
    /**
     * Документ удостоверяющий личность клиента
     */
    @Schema(description = "Номер документа удостоверяющего личность гостя", example = "146754672")
    @Size(min = 3, max = 20, message = "количество символов 3-20")
    @Pattern(
            regexp = "^\\d+$",
            message = "Некорректный номер документа удостоверяющего личность. Пример: 1320456473"
    )
    private String numberDocument;
}
