package com.asv.models.usertypedto;


import com.asv.models.enums.UserRole;
import com.asv.models.jobtypedto.JobTypeDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
/**
 * DTO, представляющий тип пользователя (роль) в системе управления отелем.
 * <p>
 * Описывает должность или категорию пользователя (например, "Клиент", "Уборщик"),
 * включая её название, описание, системную роль, статус активности и связанный набор выполняемых работ.
 * </p>
 * <p>
 * Используется для передачи данных через REST API. Поле {@code jobTypeList} исключено из JSON-сериализации
 * с помощью {@link JsonIgnore}, так как предназначено только для внутреннего использования или отдельных эндпоинтов.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Schema(description = "Модель данных типа пользователя с описание и статусом активности в системе данного типа" +
        ", списком возможных выполняемых работ ,модель передачи данных через JSON")
public class UserTypeDTO {
    /**
     * Название типа пользователя (например, "Администратор", "Клиент", "Повар").
     * <p>
     * Должно содержать от 3 до 100 символов и может включать буквы (кириллица/латиница),
     * цифры, пробелы и дефисы.
     */
    @Schema(description = "название должности или клиент- пользователя в системе", example = "Супер-Человек")
    @NotBlank(message = "название должности или клиент-пользователя в системе не должна быть пустым role")
    @Size(min = 3,max = 100,message = "количество символов 3-100")
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9\\s-]+$", message = "Роль может содержать только буквы,дефис, цифры и пробелы")
    private String name;
    /**
     * Описание функциональности и обязанностей, связанных с данным типом пользователя.
     * <p>
     * Пример: "Бронирование номеров, заказ еды, просмотр счета".
     * Длина — от 3 до 100 символов.
     */
    @Schema(description = "описание название должности или клиент в системе отеле", example = "заказ номера и еды , обычные функции")
    @NotBlank(message = "описание название должности или клиент  не должно быть пустым description")
    @Size(min = 3,max = 100,message = "количество символов 3-100")
    private String description;
    /**
     * Системная роль пользователя, определяющая его права доступа в приложении.
     * <p>
     * Возможные значения: {@code CLIENT}, {@code MANAGER}, {@code ADMIN}, {@code STAFF}.
     */
    @Schema(description = "Роль в системе управления отелем", allowableValues = {"CLIENT","MANAGER", "ADMIN", "STAFF"},example = "CLIENT")
    @NotNull
    private UserRole role;
    /**
     * Флаг, указывающий, активен ли данный тип пользователя в системе.
     * <p>
     * Если {@code false}, пользователи с этим типом не могут выполнять действия или создаваться.
     */
    @Schema(description = "активна данная должнность в системе или отменена", example = "true", allowableValues = {"true", "false"} )
    @NotNull(message = "Поле isActive обязательно")
     private Boolean isActive;
    /**
     * Список видов работ (обязанностей), которые может выполнять пользователь данного типа.
     * <p>
     * Для клиентов обычно {@code null} или пустой набор.
     * Поле исключено из JSON-сериализации с помощью {@link JsonIgnore} и помечено как
     * доступное только для чтения в OpenAPI.
     */
    @Schema(description = "какие работы выполняет пользователь в системе отеля ,для клиента null , JobType",accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    private Set<JobTypeDTO> jobTypeList ;

   }
