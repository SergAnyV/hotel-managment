package com.asv.models.roomdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO для представления базовой информации о номере отеля, полученной из базы данных.
 * <p>
 * Используется при чтении данных о комнате из хранилища и передаче их в вышестоящие слои
 * приложения (например, в сервисы или контроллеры). Не содержит валидационных ограничений,
 * так как предназначен для отображения уже сохранённых сущностей.
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RoomSimpleDataBaseDTO {
    /**
     * Уникальный идентификатор номера в формате строки (например, "101", "A205").
     */
    @Schema(description = "номер комнаты", example = "101")
    private String number;
    /**
     * Категория номера, определяющая уровень комфорта и оснащения.
     * <p>
     * Возможные значения: {@code ECONOM}, {@code STANDART}, {@code LUXE}, {@code DELUXE}.
     */
    @Schema(description = "Тип комнаты (стандарт, люкс и т.д.)", allowableValues = {"ECONOM", "STANDART", "LUXE", "DELUXE"})
    private String type;
    /**
     * Текстовое описание номера, включающее информацию об удобствах, виде из окна и других особенностях.
     */
    @Schema(description = "Описание комнаты и удобств", example = "Номер с видом на море")
    private String description;
    /**
     * Максимальное количество гостей, которое может разместиться в номере.
     */
    @Schema(description = "Вместимость (количество человек)", example = "2", type = "integer")
    private Integer capacity;
    /**
     * Стоимость проживания за одну ночь в указанной валюте (например, рублях).
     */
    @Schema(description = "Цена за ночь", example = "1500.00")
    private BigDecimal pricePerNight;
}
