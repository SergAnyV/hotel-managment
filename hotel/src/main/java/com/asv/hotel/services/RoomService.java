package com.asv.hotel.services;

import com.asv.hotel.dto.roomdto.RoomDTO;
import com.asv.hotel.entities.enums.RoomType;

import java.util.List;

/**
 * Сервис для управления номерами отеля.
 * <p>
 * Предоставляет методы для создания, поиска, обновления и удаления номеров,
 * а также для фильтрации по типу номера.
 * </p>
 */
public interface RoomService {

    /**
     * Возвращает список всех номеров отеля.
     *
     * @return список DTO всех номеров; пустой список, если номера отсутствуют
     */
    List<RoomDTO> findAllRoomsDTO();

    /**
     * Находит номер по текстовому идентификатору (номеру комнаты).
     *
     * @param number текстовый номер комнаты (например: "101", "ЛЮКС-3")
     * @return DTO номера или {@code null}, если не найден
     */
    RoomDTO findRoomDTOByNumber(String number);

    /**
     * Находит все номера указанного типа.
     *
     * @param type тип номера (например: STANDARD, LUXURY)
     * @return список DTO номеров указанного типа
     */
    List<RoomDTO> findRoomsDTOByType(RoomType type);

    /**
     * Создаёт новый номер отеля.
     *
     * @param roomDTO данные нового номера
     * @return DTO созданного номера
     */
    RoomDTO createRoom(RoomDTO roomDTO);

    /**
     * Обновляет данные существующего номера.
     *
     * @param newRoomDTO обновлённые данные номера
     * @return DTO обновлённого номера
     */
    RoomDTO changeDataRoom(RoomDTO newRoomDTO);

    /**
     * Удаляет номер отеля по текстовому идентификатору.
     *
     * @param number текстовый номер комнаты
     */
    void deleteRoomByNumber(String number);


}
