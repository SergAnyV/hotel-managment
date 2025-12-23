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

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomInternalService {
    private final RoomRepository roomRepository;


    @Transactional(readOnly = true)
    public List<RoomDTO> findAllRoomsDTO() {
        return roomRepository.findAll().stream().map(room -> RoomMapper.INSTANCE.roomToRoomDTO(room))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomDTO findRoomDTOByNumber(String number) {
        return RoomMapper.INSTANCE.roomToRoomDTO(roomRepository.findRoomByNumberLikeIgnoreCase(number).orElse(null));
    }

    @Transactional(readOnly = true)
    public List<RoomDTO> findRoomsDTOByType(RoomType type) {
        return roomRepository.findRoomByTypeLikeIgnoreCase(type).stream()
                .map(roomOptional -> RoomMapper.INSTANCE.roomToRoomDTO(roomOptional))
                .collect(Collectors.toList());
    }


    @Transactional
    public RoomDTO createRoom(RoomDTO roomDTO) {

        if (roomRepository.findRoomByNumberLikeIgnoreCase(roomDTO.getNumber()).isPresent()) {
            log.warn("Error: такая комната уже существует {} ", roomDTO.getNumber());
            throw new HotelDataAlreadyExistsException(roomDTO.getNumber());
        }
        Room room = RoomMapper.INSTANCE.roomDTOTORomm(roomDTO);
        return RoomMapper.INSTANCE.roomToRoomDTO(roomRepository.save(room));

    }


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


    @Transactional
    public void deleteRoomByNumber(String number) {
        if (roomRepository.deleteRoomByNumberLikeIgnoreCase(number) == 0) {
            throw new HotelDataNotFoundException(number);
        }
    }

    @Transactional
    public Room findRoomByNumber(String number) {
        return roomRepository.findRoomByNumberLikeIgnoreCase(number).orElse(null);
    }
    // TODO : переделать для админа и менеджера для изменений комнат см. репорт сервисы

}
