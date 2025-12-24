package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.servicehoteldto.ServiceHotelDTO;
import com.asv.hotel.dto.servicehoteldto.ServiceHotelSimpleDTO;
import com.asv.hotel.entities.ServiceHotel;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Mapper для преобразования между сущностью {@link ServiceHotel} и её DTO-представлениями.
 * <p>
 * Использует библиотеку MapStruct для генерации кода конвертации
 * между доменной моделью услуги отеля и объектами передачи данных (DTO).
 * </p>
 * <p>
 * Поддерживает следующие типы преобразований:
 * <ul>
 *   <li>Полная сущность ↔ {@link ServiceHotelDTO} (все поля, включая цену);</li>
 *   <li>Полная ↔ упрощённая DTO ({@link ServiceHotelDTO} ↔ {@link ServiceHotelSimpleDTO});</li>
 *   <li>Сущность ↔ упрощённая DTO;</li>
 *   <li>Частичное обновление сущности из полного DTO без изменения служебных полей.</li>
 * </ul>
 * </p>
 */
@Mapper
public interface ServiceHotelMapper {

    /**
     * Экземпляр маппера, созданный через фабрику MapStruct.
     * <p>
     * Предоставляет доступ к сгенерированным методам без необходимости внедрения через Spring.
     * </p>
     */
    ServiceHotelMapper INSTANCE = Mappers.getMapper(ServiceHotelMapper.class);

    /**
     * Преобразует полный DTO {@link ServiceHotelDTO} в сущность {@link ServiceHotel}.
     * <p>
     * Копирует название, описание и цену. Поля {@code createdAt} и {@code updatedAt}
     * будут инициализированы автоматически при сохранении в БД.
     * </p>
     *
     * @param serviceHotelDTO входящий DTO с полными данными об услуге
     * @return сущность для сохранения или обновления в базе данных
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    ServiceHotel serviceDTOToService(ServiceHotelDTO serviceHotelDTO);

    /**
     * Преобразует сущность {@link ServiceHotel} в полный DTO {@link ServiceHotelDTO}.
     * <p>
     * Используется при отправке данных об услуге клиенту.
     * </p>
     *
     * @param service сущность услуги из базы данных
     * @return DTO с полной информацией (включая цену)
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    ServiceHotelDTO serviceToServiceDTO(ServiceHotel service);

    /**
     * Преобразует упрощённый DTO {@link ServiceHotelSimpleDTO} в полный DTO {@link ServiceHotelDTO}.
     * <p>
     * Поля {@code price}, {@code createdAt}, {@code updatedAt} остаются неинициализированными.
     * </p>
     *
     * @param serviceHotelSimpleDTO упрощённый DTO (без цены)
     * @return полный DTO с частично заполненными данными
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    ServiceHotelDTO serviceHoteSimpleDTOToServiceHotelDTO(ServiceHotelSimpleDTO serviceHotelSimpleDTO);

    /**
     * Преобразует полный DTO {@link ServiceHotelDTO} в упрощённую версию {@link ServiceHotelSimpleDTO}.
     *
     * @param serviceHotelDTO полный DTO с ценой
     * @return упрощённый DTO (только название и описание)
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    ServiceHotelSimpleDTO serviceHotelDTOToServiceHotelSimpleDTO(ServiceHotelDTO serviceHotelDTO);

    /**
     * Преобразует сущность {@link ServiceHotel} напрямую в упрощённый DTO {@link ServiceHotelSimpleDTO}.
     *
     * @param serviceHotel сущность услуги
     * @return упрощённый DTO
     */
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    ServiceHotelSimpleDTO serviceHotelToServiceHotelSimpleDTO(ServiceHotel serviceHotel);

    /**
     * Частично обновляет существующую сущность {@link ServiceHotel} на основе данных из {@link ServiceHotelDTO}.
     * <p>
     * Особенности:
     * <ul>
     *   <li>Только ненулевые/непустые поля из DTO обновляют сущность
     *       (благодаря {@code NullValuePropertyMappingStrategy.IGNORE});</li>
     *   <li>Поля {@code createdAt} и {@code updatedAt} игнорируются — они управляются системой;</li>
     * </ul>
     * </p>
     *
     * @param serviceHotelDTO DTO с обновлёнными данными (может содержать частичную информацию)
     * @param service существующая сущность, подлежащая обновлению
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateService(ServiceHotelDTO serviceHotelDTO, @MappingTarget ServiceHotel service);


}
