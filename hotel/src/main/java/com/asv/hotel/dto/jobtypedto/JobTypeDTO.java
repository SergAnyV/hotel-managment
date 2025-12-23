package com.asv.hotel.dto.jobtypedto;

import com.asv.hotel.entities.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * Data Transfer Object (DTO), представляющий тип выполняемой работы (например, уборка, ремонт, обслуживание).
 * <p>
 * Используется для передачи данных о категории работ между клиентом и сервером,
 * например, при создании, обновлении или получении информации о типах работ в системе управления отелем.
 * </p>
 * <p>
 * Содержит валидацию входных данных с помощью Jakarta Bean Validation и аннотирован
 * для генерации OpenAPI/Swagger-документации.
 * </p>
 *
 * @see UserType
 */
@Builder
@Data
public class JobTypeDTO {
    /**
     * Название типа выполняемых работ.
     * <p>
     * Обязательное поле. Должно содержать от 3 до 100 символов.
     * Пример: "уборка", "обслуживание", "ремонт".
     * </p>
     */
    @Schema(description = "Типа выполняемых работ", example = "уборка")
    @NotBlank(message = "Тип выполняемых работ не должен быть пустым")
    @Size(min = 3, max = 100, message = "количество символов 3-100 для типа выполняемых работ")
    private String title;

    /**
     * Подробное описание типа работ.
     * <p>
     * Обязательное поле. Должно содержать от 3 до 100 символов.
     * Пример: "выполнение уборки в комнате клиентов".
     * </p>
     */
    @Schema(description = "Описание типа выполняемых работ", example = "выполнение уборки в комнате клиентов")
    @NotBlank(message = "Описание типа выполняемых работ не должен быть пустым")
    @Size(min = 3, max = 100, message = "количество символов 3-100 для описания типа выполняемых работ")
    private String description;

    /**
     * Флаг активности типа работ.
     * <p>
     * Определяет, может ли данный тип работ использоваться в системе.
     * Обязательное поле. Допустимые значения: {@code true} или {@code false}.
     * По умолчанию предполагается {@code true}.
     * </p>
     */
    @Schema(description = "Активен ли данный тип работ true/false",
            example = "true",
            defaultValue = "true",
            allowableValues = {"true", "false"})
    @NotNull(message = "активен ли тип работ должен быть указан true/false")
    private Boolean isActive;

    /**
     * Множество типов пользователей (ролей), которые могут выполнять данный тип работ.
     * <p>
     * Например: повар, горничная, техник.
     * Поле может быть пустым или содержать несколько значений типа {@link UserType}.
     * </p>
     */
    @Schema(description = "Тип  пользователя", example = "повар")
    private Set<UserType> userTypes;

}