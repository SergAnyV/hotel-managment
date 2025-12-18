package com.asv.hotel.services;

import com.asv.hotel.entities.User;
import com.asv.hotel.entities.UserType;

public interface UserInternalService extends UserService {

    User findUserByLastNameAndFirstName(String lastName, String firstName);

    User findUserByNickName(String nickName);

    UserType findUserTypeByUserNickName(String nickName);
}
