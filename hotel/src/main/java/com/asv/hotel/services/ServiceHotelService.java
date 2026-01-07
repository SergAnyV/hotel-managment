package com.asv.hotel.services;

import com.asv.hotel.dto.servicehoteldto.ServiceHotelDTO;

import java.util.List;

/**
 * Сервис для управления дополнительными сервисами отеля (например: завтрак, парковка, спа).
 * <p>
 * Предоставляет методы для создания, поиска, обновления и удаления сервисов.
 * Все операции с названием сервиса выполняются регистронезависимо.
 * </p>
 */
public interface ServiceHotelService {

    /**
     * Возвращает список всех дополнительных сервисов отеля.
     *
     * @return список DTO всех сервисов; пустой список, если сервисы отсутствуют
     */
    List<ServiceHotelDTO> findAllHotelServices();

    /**
     * Находит сервис по названию (регистронезависимо, точное совпадение).
     *
     * @param title название сервиса
     * @return DTO сервиса или {@code null}, если не найден
     */
    ServiceHotelDTO findServiceHotelDTOByTitle(String title);

    /**
     * Создаёт новый дополнительный сервис.
     *
     * @param serviceHotelDTO данные нового сервиса
     * @return DTO созданного сервиса
     */
    ServiceHotelDTO createServiceHotel(ServiceHotelDTO serviceHotelDTO);

    /**
     * Удаляет сервис по названию (регистрозависимо, точное совпадение).
     *
     * @param title название сервиса
     */
    void deletServiceHotelByTtitle(String title);

    /**
     * Обновляет данные существующего сервиса.
     *
     * @param serviceHotelDTO обновлённые данные сервиса
     * @return DTO обновлённого сервиса
     */
    ServiceHotelDTO changeDataServiceHotel(ServiceHotelDTO serviceHotelDTO);

}
