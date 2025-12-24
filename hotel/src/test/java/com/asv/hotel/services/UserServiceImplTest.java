//package com.asv.hotel.services;
//
//import com.asv.hotel.dto.userdto.UserDTO;
//import com.asv.hotel.dto.userdto.UserSimpleDTO;
//import com.asv.hotel.dto.usertypedto.UserTypeDTO;
//import com.asv.hotel.entities.User;
//import com.asv.hotel.repositories.UserRepository;
//import com.asv.hotel.repositories.UserTypeRepository;
//import com.asv.hotel.services.implementations.UserServiceImpl;
//import com.asv.hotel.services.implementations.UserTypeServiceImpl;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestConstructor;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//class UserServiceImplTest {
//    private UserServiceImpl userServiceImpl;
//    private UserRepository userRepository;
//    private UserTypeDTO testUserTypeDTO,testUserTypeDTO2;
//    private UserTypeServiceImpl userTypeServiceImpl;
//    private UserTypeRepository userTypeRepository;
//    private UserDTO testUserDTO,testUserDTO2;
//    private UserSimpleDTO testUserSimpleDTO;
//
//    public UserServiceImplTest(UserServiceImpl userServiceImpl, UserRepository userRepository,
//                               UserTypeServiceImpl userTypeServiceImpl, UserTypeRepository userTypeRepository) {
//        this.userServiceImpl = userServiceImpl;
//        this.userRepository = userRepository;
//        this.userTypeServiceImpl = userTypeServiceImpl;
//        this.userTypeRepository = userTypeRepository;
//    }
//
//    @BeforeEach
//    void setUp() {
//        testUserTypeDTO = UserTypeDTO.builder().name("Client").description("just a client").isActive(true).build();
//        testUserTypeDTO2 = UserTypeDTO.builder().name("Admin").description("just a admin").isActive(true).build();
//        testUserDTO = UserDTO.builder().role("Client").email("ghi@j.hj").firstName("Sergey")
//                .fathersName("Vladimirivich").lastName("Numm").nickName("Best").phoneNumber("86866").password("1234").build();
//        testUserSimpleDTO=UserSimpleDTO.builder().firstName("Sergey").fathersName("Vladimirivich").lastName("Numm")
//                .phoneNumber("96").build();
//        testUserDTO2 = UserDTO.builder().role("Client").email("sdgs@j.hj").firstName("Nikola")
//                .fathersName("Vladimirivich").lastName("Ginn").nickName("YoloPuki").phoneNumber("86866").password("1234").build();
//
//    }
//
//    @AfterEach
//    void tearDown() {
//        userRepository.deleteAll();
//        userTypeRepository.deleteAll();
//    }
//
//    @Test
//    void createUserShoulBeSavedNewUser() {
//        userTypeServiceImpl.createUserType(testUserTypeDTO);
//        UserDTO nudto = userServiceImpl.createUser(testUserDTO);
//        assertEquals(nudto.getFirstName(), testUserDTO.getFirstName());
//    }
//
//    @Test
//    void findUserByLastNameAndFirstNameShoudBeFindUserDTODTO() {
//        userTypeServiceImpl.createUserType(testUserTypeDTO);
//        userServiceImpl.createUser(testUserDTO);
//        UserDTO nudto =  userServiceImpl.findUserDTOByLastNameAndFirstName(testUserDTO.getLastName(), testUserDTO.getFirstName());
//        assertEquals(testUserDTO.getLastName(),nudto.getLastName());
//        assertEquals(testUserDTO.getFirstName(),nudto.getFirstName());
//
//    }
//
//    @Test
//    void findUserByLastNameAndFirstNameDTO() {
//        userTypeServiceImpl.createUserType(testUserTypeDTO);
//        userServiceImpl.createUser(testUserDTO);
//        User user= userServiceImpl.findUserByLastNameAndFirstName(testUserSimpleDTO.getLastName()
//                , testUserSimpleDTO.getFirstName());
//        assertEquals(user.getFirstName(),testUserSimpleDTO.getFirstName());
//
//    }
//
//    @Test
//    void changeDataUserByUserDTOShoildBeUpdated(){
//        userTypeServiceImpl.createUserType(testUserTypeDTO);
//        userTypeServiceImpl.createUserType(testUserTypeDTO2);
//        userServiceImpl.createUser(testUserDTO);
//        testUserDTO.setRole("Admin");
//        userServiceImpl.changeDataUser(testUserDTO);
//        var updatedUser= userServiceImpl.findUserByLastNameAndFirstName(testUserDTO.getLastName(), testUserDTO.getFirstName());
//        assertFalse(testUserDTO.getRole().equals("Client"));
//        assertTrue(testUserDTO.getFirstName().equals(updatedUser.getFirstName()));
//
//
//    }
//}