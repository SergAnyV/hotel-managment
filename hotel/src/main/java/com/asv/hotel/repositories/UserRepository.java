package com.asv.hotel.repositories;

import com.asv.hotel.entities.User;
import com.asv.hotel.entities.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE last_name ILIKE :last_name AND first_name ILIKE :first_name LIMIT 1", nativeQuery = true)
    Optional<User> findUserByLastNameAndFirstName(@Param("last_name") String lastName
            , @Param("first_name") String firstName);

    @Modifying
    @Query(value = "DELETE FROM users WHERE last_name ILIKE :last_name AND first_name ILIKE :first_name", nativeQuery = true)
    int deleteUserByLastNameAndFirstName(@Param("last_name") String lastName
            , @Param("first_name") String firstName);

    @Query(value = "SELECT * FROM users WHERE phone ILIKE :phone", nativeQuery = true)
    Optional<User> findUserByPhoneNumber(@Param("phone") String phone);

    @Modifying
    @Query(value = """
            UPDATE users SET nick_name = :nickName, first_name = :firstName, fathers_name = :fathersName, last_name = :lastName,
                email = :email, phone = :phoneNumber, password = :password, 
                    role_id = (SELECT id FROM user_types WHERE role ILIKE :role)
            WHERE id = :userId
            """, nativeQuery = true)
    int updateUser(
            @Param("userId") Long userId,
            @Param("nickName") String nickName,
            @Param("firstName") String firstName,
            @Param("fathersName") String fathersName,
            @Param("lastName") String lastName,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("password") String password,
            @Param("role") String role);

    @Query(value = "SELECT * FROM users WHERE nick_name = :nickName", nativeQuery = true)
    Optional<User> findUserByNickName(@Param("nickName") String nickName);

    @Query(value = """
            SELECT * FROM user_types 
            WHERE id= ( SELECT role_id FROM users 
                        WHERE nick_name =:nickname )
            """, nativeQuery = true
    )
    Optional<UserType> findUserTypeByUserNickName(@Param("nickname") String nickname);

    @Query(value = "SELECT * FROM users WHERE verification_token = :token", nativeQuery = true)
    Optional<User> findUserByToken(@Param("token") String token);
}
