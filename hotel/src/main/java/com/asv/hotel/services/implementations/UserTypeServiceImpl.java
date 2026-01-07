package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.usertypedto.UserTypeDTO;
import com.asv.hotel.dto.mapper.UserTypeMapper;
import com.asv.hotel.entities.UserType;
import com.asv.hotel.exceptions.HotelDataAlreadyExistsException;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.repositories.UserTypeRepository;
import com.asv.hotel.services.UserTypeInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления типами пользователей (ролями).
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>создание новых ролей с проверкой уникальности названия;</li>
 *   <li>получение списка всех ролей;</li>
 *   <li>поиск роли по названию (регистронезависимо);</li>
 *   <li>обновление данных существующей роли;</li>
 *   <li>удаление роли по названию или всех ролей сразу;</li>
 *   <li>поиск активных ролей, привязанных к типу должности (JobType).</li>
 * </ul>
 * </p>
 * <p>
 * Все операции выполняются в транзакциях для обеспечения целостности данных.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserTypeServiceImpl implements UserTypeInternalService {

    private final UserTypeRepository userTypeRepository;

    /**
     * Создаёт новый тип пользователя (роль).
     * <p>
     * Перед сохранением проверяется, не существует ли роль с таким названием (регистронезависимо).
     * Если существует — выбрасывается исключение.
     * </p>
     *
     * @param userTypeDTO данные новой роли
     * @return DTO созданной роли
     * @throws HotelDataAlreadyExistsException если роль с таким названием уже существует
     */
    @Transactional
    public UserTypeDTO createUserType(UserTypeDTO userTypeDTO) {
        if (userTypeRepository.findUserTypeByNameLikeIgnoreCase(userTypeDTO.getName().trim()).isPresent()) {
            log.warn("Error: такая роль уже существует {} ", userTypeDTO.getName());
            throw new HotelDataAlreadyExistsException(userTypeDTO.getName());
        }
        UserType userType = UserTypeMapper.INSTANCE.UserTypeDTOToUserType(userTypeDTO);
        return UserTypeMapper.INSTANCE.userTypeToUserTypeDTO(userTypeRepository.save(userType));
    }

    /**
     * Возвращает список всех типов пользователей (ролей).
     *
     * @return список DTO всех ролей; пустой список, если роли отсутствуют
     */
    @Transactional
    public List<UserTypeDTO> findAllUserTypeDTOs() {
        return userTypeRepository.findAll().stream()
                .map(userType -> UserTypeMapper.INSTANCE.userTypeToUserTypeDTO(userType))
                .collect(Collectors.toList());
    }

    /**
     * Удаляет роль по названию (регистронезависимо, частичное совпадение).
     * <p>
     * ⚠️ Используется {@code ILIKE}, поэтому может удалить роль даже при частичном совпадении.
     * </p>
     *
     * @param name название роли
     * @throws HotelDataNotFoundException если роль с указанным названием не найдена
     */
    // TODO : переделать для админа и менеджера для изменений см. репорт сервисы
    @Transactional
    public void deleteUserTypeByType(String name) {
        if (userTypeRepository.deleteByName(name) == 0) {
            log.warn("Error: такая роль не существует {} ", name);
            throw new HotelDataNotFoundException(String.format("Данной роли не существует для удаления '%s'", name));
        }
    }

    /**
     * Удаляет все типы пользователей из системы.
     * <p>
     * ⚠️ Опасная операция — используйте с осторожностью (только для тестов или миграций).
     * </p>
     */
    @Transactional
    public void deleteAllUserTypes() {
        userTypeRepository.deleteAll();
    }

    /**
     * Находит роль по названию (регистронезависимо, частичное совпадение).
     *
     * @param name название роли
     * @return DTO роли или {@code null}, если не найдена
     */
    @Transactional
    public UserTypeDTO findUserTypeDTOByType(String name) {
        Optional<UserType> userTypeOptional = userTypeRepository.findUserTypeByNameLikeIgnoreCase(name);
        return UserTypeMapper.INSTANCE.userTypeToUserTypeDTO(userTypeOptional.orElse(null));
    }

    /**
     * Возвращает сущность роли по названию (регистронезависимо, частичное совпадение).
     *
     * @param role название роли
     * @return сущность {@link UserType} или {@code null}, если не найдена
     */
    @Transactional
    public UserType findUserTypeByType(String role) {
        return userTypeRepository.findUserTypeByNameLikeIgnoreCase(role).orElse(null);
    }

    /**
     * Обновляет данные существующей роли.
     * <p>
     * Роль идентифицируется по названию (регистронезависимо).
     * </p>
     *
     * @param userTypeDTO обновлённые данные роли
     * @return DTO обновлённой роли
     * @throws HotelDataNotFoundException если роль не найдена
     */
    @Transactional
    public UserTypeDTO cahngeDataUserType(UserTypeDTO userTypeDTO) {
        Optional<UserType> userTypeOptional = userTypeRepository.findUserTypeByNameLikeIgnoreCase(userTypeDTO.getName().trim());
        if (userTypeOptional.isEmpty()) {
            log.warn("Error: роль не распознана среди доступных ,указана {}", userTypeDTO.getName());
            throw new HotelDataNotFoundException(String.format("Данная роль не распознана в базе '%s'",
                    userTypeDTO.getName()));
        }
        UserType userType = userTypeOptional.get();
        UserTypeMapper.INSTANCE.updateuserTypeFromuserTypeDTO(userTypeDTO, userType);
        userTypeRepository.save(userType);
        return UserTypeMapper.INSTANCE.userTypeToUserTypeDTO(userType);
    }

    /**
     * Находит активную роль по названию.
     * <p>
     * Возвращает роль только если она существует и её статус {@code isActive = true}.
     * </p>
     *
     * @param role название роли
     * @return сущность {@link UserType}, если роль активна
     * @throws HotelDataNotFoundException если роль не найдена или неактивна
     */
    public UserType findActiveUserTypeByType(String role) {
        Optional<UserType> userTypeOptional = userTypeRepository.findUserTypeByNameLikeIgnoreCase(role);
        if (userTypeOptional.isEmpty() || !userTypeOptional.get().getIsActive()) {
            log.warn("Error: роль не распознана среди доступных(активных) ,указана {}", role);
            throw new HotelDataNotFoundException(String.format("Данная роль не распознана в базе '%s'",
                    role));
        }
        return userTypeOptional.get();
    }

    /**
     * Возвращает все роли, которые могут занимать должность с указанным ID.
     *
     * @param jobTypeId идентификатор типа должности (JobType)
     * @return список ролей, связанных с указанной должностью
     */
    @Override
    public List<UserType> findUserTypesByJobTypeId(Long jobTypeId) {
        return userTypeRepository.findUserTypesByJobTypeId(jobTypeId);
    }
}
