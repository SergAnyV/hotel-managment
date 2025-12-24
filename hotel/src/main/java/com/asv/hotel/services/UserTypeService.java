package com.asv.hotel.services;

import com.asv.hotel.dto.usertypedto.UserTypeDTO;
import com.asv.hotel.entities.UserType;

import java.util.List;

public interface UserTypeService {

    UserTypeDTO createUserType(UserTypeDTO userTypeDTO);

    List<UserTypeDTO> findAllUserTypeDTOs();

    void deleteUserTypeByType(String role);

    void deleteAllUserTypes();

    UserTypeDTO findUserTypeDTOByType(String role);

    UserType findUserTypeByType(String role);

    UserTypeDTO cahngeDataUserType(UserTypeDTO userTypeDTO);

    UserType findActiveUserTypeByType(String role);



}
