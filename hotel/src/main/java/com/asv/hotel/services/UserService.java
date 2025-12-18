package com.asv.hotel.services;

import com.asv.hotel.dto.userdto.UserDTO;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO findUserDTOByLastNameAndFirstName(String lastName, String firstName);

    void deleteUserByLastNameAndFirstName(String lastName, String firstName);

    UserDTO findUserDTOByPhoneNumber(String phoneNumber);

    UserDTO changeDataUser(UserDTO userDTO);

    Boolean confirmRegistrationUser(String token);

}
