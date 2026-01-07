package com.asv.hotel.services;

import com.asv.hotel.entities.ServiceHotel;

/**
 * Расширенный интерфейс сервиса управления дополнительными сервисами отеля.
 * <p>
 * Предназначен для внутреннего использования в слое сервисов (а не в контроллерах).
 * Добавляет методы, возвращающие сущности {@link ServiceHotel} напрямую,
 * в отличие от публичного интерфейса, оперирующего только DTO.
 * </p>
 */
public interface ServiceHotelInternalService extends ServiceHotelService{

    /**
     * Находит сущность дополнительного сервиса по названию (регистронезависимо, точное совпадение).
     *
     * @param title название сервиса
     * @return сущность {@link ServiceHotel} или {@code null}, если не найдена
     */
    ServiceHotel findServiceHotelByTitle(String title);


}
