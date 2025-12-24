package com.asv.hotel.services;

import com.asv.hotel.dto.roomdto.RoomDTO;
import com.asv.hotel.entities.enums.RoomType;

import java.util.List;

public interface RoomService {

    List<RoomDTO> findAllRoomsDTO();

    RoomDTO findRoomDTOByNumber(String number);

    List<RoomDTO> findRoomsDTOByType(RoomType type);

    RoomDTO createRoom(RoomDTO roomDTO);

    RoomDTO changeDataRoom(RoomDTO newRoomDTO);

    void deleteRoomByNumber(String number);


}
