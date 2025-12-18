package com.asv.hotel.services;

import com.asv.hotel.entities.ServiceHotel;

public interface ServiceHotelInternalService extends ServiceHotelService{

    ServiceHotel findServiceHotelByTitle(String title);


}
