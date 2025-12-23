package com.asv.hotel.services;

import com.asv.hotel.entities.Room;

public interface RoomInternalService extends RoomService{
    Room findRoomByNumber(String number);


}
