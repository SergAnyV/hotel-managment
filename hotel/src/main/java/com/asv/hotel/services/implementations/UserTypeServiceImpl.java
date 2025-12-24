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

@Service
@Slf4j
@RequiredArgsConstructor
public class UserTypeServiceImpl implements UserTypeInternalService {

    private final UserTypeRepository userTypeRepository;

    @Transactional
    public UserTypeDTO createUserType(UserTypeDTO userTypeDTO) {
        if (userTypeRepository.findUserTypeByNameLikeIgnoreCase(userTypeDTO.getName().trim()).isPresent()) {
            log.warn("Error: такая роль уже существует {} ", userTypeDTO.getName());
            throw new HotelDataAlreadyExistsException(userTypeDTO.getName());
        }
        UserType userType = UserTypeMapper.INSTANCE.UserTypeDTOToUserType(userTypeDTO);
        return UserTypeMapper.INSTANCE.userTypeToUserTypeDTO(userTypeRepository.save(userType));
    }

    @Transactional
    public List<UserTypeDTO> findAllUserTypeDTOs() {
        return userTypeRepository.findAll().stream()
                .map(userType -> UserTypeMapper.INSTANCE.userTypeToUserTypeDTO(userType))
                .collect(Collectors.toList());
    }
    // TODO : переделать для админа и менеджера для изменений см. репорт сервисы
    @Transactional
    public void deleteUserTypeByType(String name) {
        if (userTypeRepository.deleteByName(name) == 0) {
            log.warn("Error: такая роль не существует {} ", name);
            throw new HotelDataNotFoundException(String.format("Данной роли не существует для удаления '%s'", name));
        }
    }

    @Transactional
    public void deleteAllUserTypes() {
        userTypeRepository.deleteAll();
    }

    @Transactional
    public UserTypeDTO findUserTypeDTOByType(String name) {
        Optional<UserType> userTypeOptional = userTypeRepository.findUserTypeByNameLikeIgnoreCase(name);
        return UserTypeMapper.INSTANCE.userTypeToUserTypeDTO(userTypeOptional.orElse(null));
    }

    @Transactional
    public UserType findUserTypeByType(String role) {
        return userTypeRepository.findUserTypeByNameLikeIgnoreCase(role).orElse(null);
    }

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


    public UserType findActiveUserTypeByType(String role) {
        Optional<UserType> userTypeOptional = userTypeRepository.findUserTypeByNameLikeIgnoreCase(role);
        if (userTypeOptional.isEmpty() || !userTypeOptional.get().getIsActive()) {
            log.warn("Error: роль не распознана среди доступных(активных) ,указана {}", role);
            throw new HotelDataNotFoundException(String.format("Данная роль не распознана в базе '%s'",
                    role));
        }
        return userTypeOptional.get();
    }

    @Override
    public List<UserType> findUserTypesByJobTypeId(Long jobTypeId) {
        return userTypeRepository.findUserTypesByJobTypeId(jobTypeId);
    }
}
