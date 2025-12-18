package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.mapper.ServiceHotelMapper;
import com.asv.hotel.dto.servicehoteldto.ServiceHotelDTO;
import com.asv.hotel.entities.ServiceHotel;
import com.asv.hotel.exceptions.HotelDataAlreadyExistsException;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.repositories.ServiceHotelRepository;
import com.asv.hotel.services.ServiceHotelInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceHotelServiceImpl implements ServiceHotelInternalService {
    private final ServiceHotelRepository serviceHotelRepository;

    @Transactional
    public List<ServiceHotelDTO> findAllHotelServices() {
        return serviceHotelRepository.findAll().stream().map(service ->
                ServiceHotelMapper.INSTANCE.serviceToServiceDTO(service)
        ).collect(Collectors.toList());
    }

    @Transactional
    public ServiceHotelDTO findServiceHotelDTOByTitle(String title) {

        Optional<ServiceHotel> serviceOptional =
                serviceHotelRepository.findByTitle(title);
        if (serviceOptional.isEmpty()) {
            return null;
        }
        return ServiceHotelMapper.INSTANCE.serviceToServiceDTO(serviceOptional.get());
    }

    @Transactional
    public ServiceHotelDTO createServiceHotel(ServiceHotelDTO serviceHotelDTO) {
        if (serviceHotelRepository.findByTitle(serviceHotelDTO.getTitle()).isPresent()) {
            log.warn("War такой сервис уже существует поиск по названи=ю {}", serviceHotelDTO);
            throw new HotelDataAlreadyExistsException("такой сервис уже существует в базе");
        }
        return ServiceHotelMapper.INSTANCE.serviceToServiceDTO(
                serviceHotelRepository.save(
                        ServiceHotelMapper.INSTANCE.serviceDTOToService(serviceHotelDTO)));
    }

    @Transactional
    public void deletServiceHotelByTtitle(String title) {
        if (serviceHotelRepository.deleteByTitle(title) == 0) {
            log.error("Error данного типа сервиса не найдено {} при попытки удаления сервиса", title);
            throw new HotelDataNotFoundException("ошибка при удаление сервиса из базы");
        }
    }

    // TODO : переделать для админа и менеджера для изменений см. репорт сервисы
    @Transactional
    public ServiceHotelDTO changeDataServiceHotel(ServiceHotelDTO serviceHotelDTO) {
        Optional<ServiceHotel> serviceHotelOptional = serviceHotelRepository.findByTitle(serviceHotelDTO.getTitle());
        if (serviceHotelOptional.isEmpty()) {
            log.error("Error не найден такой сервис для обновления {}", serviceHotelDTO);
            throw new HotelDataNotFoundException("there is no this service");
        }
        ServiceHotel serviceHotel = serviceHotelOptional.get();
        ServiceHotelMapper.INSTANCE.updateService(serviceHotelDTO, serviceHotel);
        return ServiceHotelMapper.INSTANCE.serviceToServiceDTO(serviceHotelRepository.save(serviceHotel));
    }

    public ServiceHotel findServiceHotelByTitle(String title) {
        return serviceHotelRepository.findByTitle(title).orElse(null);
    }
}
