package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.userdto.UserDTO;
import com.asv.hotel.dto.mapper.UserMapper;
import com.asv.hotel.entities.User;
import com.asv.hotel.entities.UserType;
import com.asv.hotel.exceptions.HotelDataAlreadyExistsException;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.repositories.UserRepository;
import com.asv.hotel.services.EmailService;
import com.asv.hotel.services.NotificationHotelService;
import com.asv.hotel.services.UserInternalService;
import com.asv.hotel.services.UserTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserInternalService {
    private final UserRepository userRepository;
    private final UserTypeService userTypeService;
    private final EmailService emailService;
    private final NotificationHotelService notificationHotelService;
    private static final Boolean VERIFICATION_STATUS_FALSE = Boolean.FALSE;
    private static final Boolean VERIFICATION_STATUS_TRUE = Boolean.TRUE;
    private static final String REGISTRATION_SUBJECT = "Registration mail";
    private static final String URL_REGISTRATION_USER="http://localhost:8083/users/verify?token=";
    private static final String CLIENT="клиент";

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {

        if (!userRepository.findUserByLastNameAndFirstName(userDTO.getLastName(), userDTO.getFirstName()).isEmpty()) {
            log.warn("Error: такой user уже существует {} {}", userDTO.getFirstName(), userDTO.getLastName());
            throw new HotelDataAlreadyExistsException(
                    String.format("Такой user уже существует '%s'  '%s'", userDTO.getFirstName(), userDTO.getLastName()));
        }

        UserType userType = userTypeService.findUserTypeByType(userDTO.getType());
        if (userType == null) {
            userType = userTypeService.findUserTypeByType(CLIENT);
        }

        User user = UserMapper.INSTANCE.userDTOToUser(userDTO);
        user.setType(userType);
        user.setVerifyStatus(VERIFICATION_STATUS_FALSE);

        String token= generateRandomToken();
        user.setVerificationToken(token);

        User savedUser = userRepository.save(user);

        notificationHotelService.createNotificationNewUserVerifying(URL_REGISTRATION_USER.concat(token),
                user, REGISTRATION_SUBJECT);

        return UserMapper.INSTANCE.userToUserDTO(savedUser);
    }

    @Transactional
    public UserDTO findUserDTOByLastNameAndFirstName(String lastName, String firstName) {
        return UserMapper.INSTANCE.userToUserDTO(
                userRepository.findUserByLastNameAndFirstName(lastName, firstName).orElse(null));
    }

    @Override
    public User findUserByLastNameAndFirstName(String lastName, String firstName) {
        return userRepository.findUserByLastNameAndFirstName(lastName, firstName).orElse(null);
    }

    @Override
    public User findUserByNickName(String nickName) {
        return userRepository.findUserByNickName(nickName).orElse(null);
    }

    @Override
    public UserType findUserTypeByUserNickName(String nickName) {
        return userRepository.findUserTypeByUserNickName(nickName).orElse(null);
    }
    // TODO : переделать для админа и менеджера для изменений см. репорт сервисы
    @Transactional
    public void deleteUserByLastNameAndFirstName(String lastName, String firstName) {
        if (userRepository.deleteUserByLastNameAndFirstName(lastName, firstName) == 0) {
            log.warn("Error : такого юзера не существует для удаления");
            throw new HotelDataNotFoundException("Такого юзера не существует");
        }
    }

    @Transactional
    public UserDTO findUserDTOByPhoneNumber(String phoneNumber) {
        return UserMapper.INSTANCE.userToUserDTO(userRepository.findUserByPhoneNumber(phoneNumber).orElse(null));
    }

    @Transactional
    public UserDTO changeDataUser(UserDTO userDTO) {
        User existingUser = userRepository.findUserByLastNameAndFirstName(userDTO.getLastName(), userDTO.getFirstName())
                .orElseThrow(() -> new HotelDataNotFoundException("User not found"));
        UserMapper.INSTANCE.updateUserFromDto(userDTO, existingUser, userTypeService);
        userRepository.save(existingUser);
        return UserMapper.INSTANCE.userToUserDTO(existingUser);
    }

    @Transactional
    public Boolean confirmRegistrationUser(String token){
        User user=userRepository.findUserByToken(token).orElse(null);
        if(user==null){
            return VERIFICATION_STATUS_FALSE;
        }
        user.setVerifyStatus(VERIFICATION_STATUS_TRUE);
        userRepository.save(user);
        return VERIFICATION_STATUS_TRUE;
    }

    private String generateRandomToken() {
        return UUID.randomUUID().toString();
    }
}
