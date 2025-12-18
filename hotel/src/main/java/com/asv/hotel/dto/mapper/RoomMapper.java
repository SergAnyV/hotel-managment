package com.asv.hotel.dto.mapper;


import com.asv.hotel.dto.roomdto.RoomDTO;
import com.asv.hotel.dto.roomdto.RoomSimpleDTO;
import com.asv.hotel.entities.Room;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
/**
 * Mapper для преобразования между сущностью {@link Room} и её DTO-представлениями.
 * <p>
 * Использует библиотеку MapStruct для генерации
 * между доменной моделью номера отеля и объектами передачи данных (DTO).
 * </p>
 * <p>
 * Поддерживает следующие сценарии:
 * <ul>
 *   <li>Полное преобразование сущности ↔ {@link RoomDTO} (все поля, включая служебные);</li>
 *   <li>Упрощённое преобразование сущности → {@link RoomSimpleDTO} (только основные данные);</li>
 *   <li>Частичное обновление сущности из {@link RoomDTO} без изменения идентификатора и связанных коллекций.</li>
 * </ul>
 * </p>
 */
@Mapper
public interface RoomMapper {

    /**
     * Экземпляр маппера, созданный через фабрику MapStruct.
     * <p>
     * Предоставляет доступ к сгенерированным методам без необходимости внедрения через Spring.
     * </p>
     */
    RoomMapper INSTANCE= Mappers.getMapper(RoomMapper.class);

    /**
     * Преобразует сущность {@link Room} в полный DTO {@link RoomDTO}.
     * <p>
     * Копирует все поля, включая номер, тип, описание, вместимость, цену, статус доступности
     * и временные метки создания/обновления.
     * </p>
     * <p>
     * Используется при отправке полной информации о номере клиенту.
     * </p>
     *
     * @param room сущность номера из базы данных
     * @return полный DTO с информацией о номере
     */
    RoomDTO roomToRoomDTO(Room room);

    /**
     * Преобразует полный DTO {@link RoomDTO} обратно в сущность {@link Room}.
      * @param roomDTO входящий DTO с полными данными о номере
     * @return сущность для сохранения или обновления в БД
     */
    Room roomDTOTORomm(RoomDTO roomDTO);

    /**
     * Преобразует сущность {@link Room} в упрощённый DTO {@link RoomSimpleDTO}.
     * <p>
     * Копирует только основные поля: номер, тип и описание.
     * Используется в сценариях, где не требуется детальная информация (например,
     * при отображении списка номеров или в составе других DTO).
     * </p>
     *
     * @param room сущность номера
     * @return упрощённый DTO
     */
    RoomSimpleDTO roomToRoomSimpleDTO(Room room);

    /**
     * Частично обновляет существующую сущность {@link Room} на основе данных из {@link RoomDTO}.
     * <p>
     * Особенности:
     * <ul>
     *   <li>Поля {@code id}, {@code bookings}, {@code reports}, {@code createdAt} игнорируются;</li>
     *   <li>Только ненулевые/непустые значения из DTO обновляют соответствующие поля сущности
     *       (благодаря {@code NullValuePropertyMappingStrategy.IGNORE});</li>
     * </ul>
     * </p>
     * @param roomDTO DTO с обновлёнными данными (может содержать частичную информацию)
     * @param room существующая сущность, которая будет обновлена
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateRoomFromDTO(RoomDTO roomDTO, @MappingTarget Room room);


}
