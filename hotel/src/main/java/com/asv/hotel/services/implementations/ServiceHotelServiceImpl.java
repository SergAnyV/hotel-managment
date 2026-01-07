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

/**
 * Реализация сервиса управления дополнительными сервисами отеля.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>получение списка всех сервисов;</li>
 *   <li>поиск сервиса по названию (регистронезависимо, точное совпадение);</li>
 *   <li>создание нового сервиса с проверкой уникальности названия;</li>
 *   <li>обновление данных существующего сервиса;</li>
 *   <li>удаление сервиса по названию.</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceHotelServiceImpl implements ServiceHotelInternalService {
    private final ServiceHotelRepository serviceHotelRepository;

    /**
     * Возвращает список всех дополнительных сервисов отеля.
     *
     * @return список DTO всех сервисов; пустой список, если сервисы отсутствуют
     */
    @Transactional
    public List<ServiceHotelDTO> findAllHotelServices() {
        return serviceHotelRepository.findAll().stream().map(service ->
                ServiceHotelMapper.INSTANCE.serviceToServiceDTO(service)
        ).collect(Collectors.toList());
    }

    /**
     * Находит сервис по названию (точное совпадение, регистронезависимо).
     *
     * @param title название сервиса
     * @return DTO сервиса или {@code null}, если не найден
     */
    @Transactional
    public ServiceHotelDTO findServiceHotelDTOByTitle(String title) {

        Optional<ServiceHotel> serviceOptional =
                serviceHotelRepository.findByTitle(title);
        if (serviceOptional.isEmpty()) {
            return null;
        }
        return ServiceHotelMapper.INSTANCE.serviceToServiceDTO(serviceOptional.get());
    }

    /**
     * Создаёт новый дополнительный сервис.
     * <p>
     * Перед сохранением проверяется, не существует ли сервис с таким названием.
     * Если существует — выбрасывается исключение.
     * </p>
     *
     * @param serviceHotelDTO данные нового сервиса
     * @return DTO созданного сервиса
     * @throws HotelDataAlreadyExistsException если сервис с таким названием уже существует
     */
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

    /**
     * Удаляет сервис по названию (точное совпадение, регистрозависимо).
     * <p>
     * ⚠️ Обратите внимание: в репозитории используется точное сравнение ({@code =}),
     * в отличие от поиска, который использует {@code ILIKE}.
     * </p>
     *
     * @param title название сервиса
     * @throws HotelDataNotFoundException если сервис с указанным названием не найден
     */
    @Transactional
    public void deletServiceHotelByTtitle(String title) {
        if (serviceHotelRepository.deleteByTitle(title) == 0) {
            log.error("Error данного типа сервиса не найдено {} при попытки удаления сервиса", title);
            throw new HotelDataNotFoundException("ошибка при удаление сервиса из базы");
        }
    }

    /**
     * Обновляет данные существующего сервиса.
     * <p>
     * Сервис идентифицируется по названию. Все поля из DTO копируются в существующую сущность.
     * </p>
     *
     * @param serviceHotelDTO обновлённые данные сервиса
     * @return DTO обновлённого сервиса
     * @throws HotelDataNotFoundException если сервис с указанным названием не найден
     */
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

    /**
     * Возвращает сущность сервиса по названию (точное совпадение, регистронезависимо).
     *
     * @param title название сервиса
     * @return сущность {@link ServiceHotel} или {@code null}, если не найдена
     */
    public ServiceHotel findServiceHotelByTitle(String title) {
        return serviceHotelRepository.findByTitle(title).orElse(null);
    }
}
