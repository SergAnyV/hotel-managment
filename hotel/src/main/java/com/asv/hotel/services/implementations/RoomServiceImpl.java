package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.mapper.RoomMapper;
import com.asv.hotel.dto.roomdto.RoomDTO;
import com.asv.hotel.entities.Room;
import com.asv.hotel.entities.enums.RoomType;
import com.asv.hotel.exceptions.HotelDataAlreadyExistsException;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.repositories.RoomRepository;
import com.asv.hotel.services.RoomInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления номерами отеля.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>получение списка всех номеров;</li>
 *   <li>поиск номера по точному или частичному совпадению номера комнаты;</li>
 *   <li>поиск номеров по типу (например: STANDARD, LUXURY);</li>
 *   <li>создание нового номера с проверкой уникальности;</li>
 *   <li>обновление данных существующего номера;</li>
 *   <li>удаление номера по номеру комнаты.</li>
 * </ul>
 * </p>
 * <p>
 * Все операции чтения помечены как {@code readOnly = true} для оптимизации транзакций.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomInternalService {
    private final RoomRepository roomRepository;

    /**
     * Возвращает список всех номеров отеля.
     *
     * @return список DTO всех номеров; пустой список, если номера отсутствуют
     */
    @Transactional(readOnly = true)
    public List<RoomDTO> findAllRoomsDTO() {
        return roomRepository.findAll().stream().map(room -> RoomMapper.INSTANCE.roomToRoomDTO(room))
                .collect(Collectors.toList());
    }

    /**
     * Находит номер по частичному или полному совпадению текстового номера (регистронезависимо).
     *
     * @param number часть или полный номер комнаты (например: "101", "люкс")
     * @return DTO номера или {@code null}, если не найден
     */
    @Transactional(readOnly = true)
    public RoomDTO findRoomDTOByNumber(String number) {
        return RoomMapper.INSTANCE.roomToRoomDTO(roomRepository.findRoomByNumberLikeIgnoreCase(number).orElse(null));
    }

    /**
     * Находит все номера указанного типа (регистронезависимо).
     *
     * @param type тип номера (например: STANDARD, LUXURY)
     * @return список DTO номеров указанного типа
     */
    @Transactional(readOnly = true)
    public List<RoomDTO> findRoomsDTOByType(RoomType type) {
        return roomRepository.findRoomByTypeLikeIgnoreCase(type).stream()
                .map(roomOptional -> RoomMapper.INSTANCE.roomToRoomDTO(roomOptional))
                .collect(Collectors.toList());
    }

    /**
     * Создаёт новый номер отеля.
     * <p>
     * Перед сохранением проверяется, не существует ли номер с таким текстовым идентификатором.
     * Если существует — выбрасывается исключение.
     * </p>
     *
     * @param roomDTO данные нового номера
     * @return DTO созданного номера
     * @throws HotelDataAlreadyExistsException если номер с таким идентификатором уже существует
     */
    @Transactional
    public RoomDTO createRoom(RoomDTO roomDTO) {

        if (roomRepository.findRoomByNumberLikeIgnoreCase(roomDTO.getNumber()).isPresent()) {
            log.warn("Error: такая комната уже существует {} ", roomDTO.getNumber());
            throw new HotelDataAlreadyExistsException(roomDTO.getNumber());
        }
        Room room = RoomMapper.INSTANCE.roomDTOTORomm(roomDTO);
        return RoomMapper.INSTANCE.roomToRoomDTO(roomRepository.save(room));

    }

    /**
     * Обновляет данные существующего номера.
     * <p>
     * Номер идентифицируется по текстовому идентификатору.
     * Все поля из DTO копируются в существующую сущность.
     * </p>
     *
     * @param newRoomDTO обновлённые данные номера
     * @return DTO обновлённого номера
     * @throws HotelDataNotFoundException если номер с указанным идентификатором не найден
     */
    @Transactional
    public RoomDTO changeDataRoom(RoomDTO newRoomDTO) {
        var existingRoom = roomRepository.findRoomByNumberLikeIgnoreCase(newRoomDTO.getNumber())
                .orElseThrow(() -> {
                    log.warn("Error:Не существует комнаты с номером {} метод update в RoomService", newRoomDTO.getNumber());
                    return new HotelDataNotFoundException("Не существует комнаты с номером " + newRoomDTO.getNumber());
                });

        RoomMapper.INSTANCE.updateRoomFromDTO(newRoomDTO, existingRoom);
        return RoomMapper.INSTANCE.roomToRoomDTO(roomRepository.save(existingRoom));
    }

    /**
     * Удаляет номер отеля по частичному или полному совпадению текстового номера (регистронезависимо).
     *
     * @param number часть или полный номер комнаты
     * @throws HotelDataNotFoundException если номер с указанным идентификатором не найден
     */
    @Transactional
    public void deleteRoomByNumber(String number) {
        if (roomRepository.deleteRoomByNumberLikeIgnoreCase(number) == 0) {
            throw new HotelDataNotFoundException(number);
        }
    }

    /**
     * Возвращает сущность номера по частичному или полному совпадению текстового номера (регистронезависимо).
     *
     * @param number часть или полный номер комнаты
     * @return сущность {@link Room} или {@code null}, если не найдена
     */
    @Transactional
    public Room findRoomByNumber(String number) {
        return roomRepository.findRoomByNumberLikeIgnoreCase(number).orElse(null);
    }
    // TODO : переделать для админа и менеджера для изменений комнат см. репорт сервисы

}
