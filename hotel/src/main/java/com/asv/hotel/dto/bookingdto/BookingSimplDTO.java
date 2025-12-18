package com.asv.hotel.dto.bookingdto;


import com.asv.hotel.entities.Guest;
import com.asv.hotel.dto.servicehoteldto.ServiceHotelSimpleDTO;
import com.asv.hotel.dto.userdto.UserSimpleDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
/**
 * Упрощённый Data Transfer Object (DTO) для представления бронирования отеля.
 * <p>
 * Используется при создании или обновлении бронирования.
 * Содержит только необходимые поля для инициализации брони без идентификатора
 * и вычисляемых/служебных атрибутов.
 * </p>
 * <p>
 * Поле {@link #userSimpleDTO} помечено как {@code READ_ONLY}, так как информация
 * о пользователе определяется на основе текущей аутентификации и не должна
 * передаваться клиентом.
 * </p>
 * <p>
 * Поддерживает валидацию входных данных с помощью Jakarta Bean Validation
 * и аннотирован для генерации OpenAPI-документации (Swagger).
 * </p>
 *
 * @see UserSimpleDTO
 * @see ServiceHotelSimpleDTO
 * @see Guest
 */
@Data
@Builder
@Schema(description = "Модель данных бронирования  с описание характеристик" +
        ",модель передачи данных через JSON")
public class BookingSimplDTO {

    /**
     * Дата заезда гостя в отель.
     * <p>
     * Обязательное поле. Должна быть сегодняшней или будущей датой.
     * </p>
     */
    @Schema(description = "Дата заезда", example = "2028-01-01")
    @NotNull(message = "Дата начала проживания обязательна")
    @FutureOrPresent
    private LocalDate checkInDate;
    /**
     * Дата выезда гостя из отеля.
     * <p>
     * Обязательное поле. Должна быть строго в будущем относительно текущей даты.
     * </p>
     */
    @Schema(description = "Дата окончания действия", example = "2029-01-01")
    @NotNull(message = "Дата выезда обязательна")
    @Future
    private LocalDate checkOutDate;
    /**
     * Количество человек, планирующих проживание в номере.
     * <p>
     * Должно быть целым числом в диапазоне от 1 до 10 включительно.
     * </p>
     */
    @Schema(description = "количество человек", example = "2", type = "integer")
    @Min(value = 1, message = "Вместимость должна быть не менее 1")
    @Max(value = 10, message = "Вместимость должна быть не более 10")
    private Integer persons;
    /**
     * Номер комнаты, которую планирует забронировать клиент.
     * <p>
     * Обязательное поле. Должно содержать только буквы (кириллица или латиница) и цифры.
     * Пробелы и специальные символы не допускаются.
     * </p>
     */
    @Schema(description = "номер комнаты", example = "101")
    @NotBlank(message = "номер комнты не должен быть пустым")
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$", message = "Комната может содержать только буквы, цифры ")
    private String roomNumber;
    /**
     * Информация о пользователе, создающем бронирование.
     * <p>
     * Поле доступно только для чтения (не должно передаваться в запросе от клиента).
     * Заполняется автоматически на основе данных аутентифицированного пользователя.
     * </p>
     */
    @Schema(description = "Описание того кто зачисляется")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserSimpleDTO userSimpleDTO;
    /**
     * Промокод, применяемый к бронированию для получения скидки.
     * <p>
     * Необязательное поле. Может быть {@code null} или пустой строкой.
     * Формат промокода определяется бизнес-логикой (в данном DTO не валидируется).
     * </p>
     */
    @Schema(description = "промокод", example = "WELCOME1000")
    private String promoCodeDTO;
    /**
     * Множество дополнительных услуг отеля, выбранных при бронировании.
     * <p>
     * Каждая услуга представлена упрощённым DTO {@link ServiceHotelSimpleDTO}.
     * Может быть пустым или {@code null}, если услуги не выбраны.
     * </p>
     */
    @Schema(description = "сервисы")
    private Set<ServiceHotelSimpleDTO> serviceSet;
    /**
     * Список гостей, которые будут проживать в забронированном номере.
     * <p>
     * Содержит полные сущности {@link Guest}.Вносятся проживающие в данном номере.
     * </p>
     */
    @Schema(description = "список гостей проживающих в номере")
    private List<Guest> guestList;

}
