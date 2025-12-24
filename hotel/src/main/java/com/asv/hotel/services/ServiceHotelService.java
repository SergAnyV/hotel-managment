package com.asv.hotel.services;

import com.asv.hotel.dto.servicehoteldto.ServiceHotelDTO;

import java.util.List;

public interface ServiceHotelService {

    List<ServiceHotelDTO> findAllHotelServices();

    ServiceHotelDTO findServiceHotelDTOByTitle(String title);

    ServiceHotelDTO createServiceHotel(ServiceHotelDTO serviceHotelDTO);

    void deletServiceHotelByTtitle(String title);

    ServiceHotelDTO changeDataServiceHotel(ServiceHotelDTO serviceHotelDTO);

}
