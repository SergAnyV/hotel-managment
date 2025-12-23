package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.notificationdto.NotificationHotelDto;
import com.asv.hotel.entities.NotificationHotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
/**
 * Mapper для преобразования между сущностью {@link NotificationHotel} и её DTO-представлением {@link NotificationHotelDto}.
 * <p>
 * Использует библиотеку MapStruct для генерации эффективного кода конвертации
 * между доменной моделью уведомлений и объектами передачи данных.
 * </p>
 * <p>
 * Подключает вспомогательные мапперы:
 * <ul>
 *   <li>{@link UserMapper} — для преобразования связанных данных пользователя;</li>
 *   <li>{@link BookingMapper} — для преобразования связанных данных бронирования.</li>
 * </ul>
 * </p>
 */
@Mapper(uses = {UserMapper.class, BookingMapper.class})
public interface NotificationMapper {
    /**
     * Статический экземпляр маппера, созданный через фабрику MapStruct.
     * <p>
     * Обеспечивает удобный доступ к методам преобразования без необходимости внедрения через Spring.
     * </p>
     */
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    /**
     * Преобразует сущность {@link NotificationHotel} в DTO {@link NotificationHotelDto}.
     * <p>
     * Особенности маппинга:
     * <ul>
     *   <li>Поле {@code nickName} извлекается из вложенной сущности {@code user.nickName};</li>
     *   <li>Поле {@code bookingId} извлекается из вложенной сущности {@code booking.id};</li>
     *   <li>Остальные совпадающие поля копируются автоматически.</li>
     * </ul>
     * </p>
     * <p>
     * Используется при отправке уведомлений клиенту.
     * </p>
     *
     * @param notificationHotel сущность уведомления из базы данных
     * @return DTO
     */
    @Mapping(target = "nickName",source = "user.nickName")
    @Mapping(target = "bookingId",source = "booking.id")
    NotificationHotelDto notificationToNotificationDTO(NotificationHotel notificationHotel);
    /**
     * Преобразует DTO {@link NotificationHotelDto} обратно в сущность {@link NotificationHotel}.
     * <p>
     * В текущей реализации копируются только базовые поля: {@code message} и {@code createdAt}.
     * Связи с {@code user} и {@code booking} не восстанавливаются автоматически —
     * они должны быть установлены вручную в сервисном слое на основе {@code nickName} и {@code bookingId}.
     * </p>
     *
     * @param notificationDto входящий DTO с данными уведомления
     * @return частично заполненная сущность уведомления
     */
    @Mapping(target = "message",source = "message")
    @Mapping(target = "createdAt",source = "createdAt")
    NotificationHotel notificationdtoToNotification(NotificationHotelDto notificationDto);
}
