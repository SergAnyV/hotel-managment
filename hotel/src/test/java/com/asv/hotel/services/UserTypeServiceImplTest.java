//package com.asv.hotel.services;
//
//import com.asv.hotel.dto.usertypedto.UserTypeDTO;
//import com.asv.hotel.exceptions.HotelDataAlreadyExistsException;
//import com.asv.hotel.repositories.UserTypeRepository;
//import com.asv.hotel.services.implementations.UserTypeServiceImpl;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestConstructor;
//
//import javax.sql.DataSource;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//class UserTypeServiceImplTest {
//    private DataSource dataSource;
//    private UserTypeDTO testUserTypeDTO, testUserTypeDTO2;
//    private UserTypeServiceImpl userTypeServiceImpl;
//    private UserTypeRepository userTypeRepository;
//
//    public UserTypeServiceImplTest(DataSource dataSource, UserTypeServiceImpl userTypeServiceImpl,
//                                   UserTypeRepository userTypeRepository) {
//        this.dataSource = dataSource;
//        this.userTypeServiceImpl = userTypeServiceImpl;
//        this.userTypeRepository = userTypeRepository;
//    }
//
//    @BeforeEach
//    public void setUp() {
//        testUserTypeDTO = UserTypeDTO.builder()
//                .name("Администратора")
//                .description("просто админ")
//                .isActive(true)
//                .build();
//        testUserTypeDTO2 = UserTypeDTO.builder()
//                .name("Работник")
//                .description("просто работник")
//                .isActive(true)
//                .build();
//    }
//
//    @AfterEach
//    public void tearDown() {
//        try {
//            userTypeRepository.deleteAll();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    void testDataSourceIsNotNull() {
//        assertThat(dataSource).isNotNull();
//    }
//
//    //сохранение новой роли в репозитории CREATE
//    @Test
//    void saveUserTypeShouldBeCreateUserTypeUserType() {
//        UserTypeDTO savedUserType = userTypeServiceImpl.createUserType(testUserTypeDTO);
//        assertEquals(savedUserType.getName(), testUserTypeDTO.getName());
//        assertEquals(savedUserType.getDescription(), testUserTypeDTO.getDescription());
//        assertEquals(savedUserType.getIsActive(), testUserTypeDTO.getIsActive());
//
//    }
//
//    @Test
//    void createUserTypeUserTypeShouldThrowIfUserTypeExist() {
//        userTypeServiceImpl.createUserType(testUserTypeDTO);
//
//        assertThatThrownBy(() -> userTypeServiceImpl.createUserType(testUserTypeDTO))
//                .isInstanceOf(HotelDataAlreadyExistsException.class)
//                .hasMessageContaining(testUserTypeDTO.getName());
//    }
//
//    //поиск всех возможных ролей
//    @Test
//    void findAllShouldBeFindAllUserTypeDTOsUserTypes() {
//        userTypeServiceImpl.createUserType(testUserTypeDTO);
//        userTypeServiceImpl.createUserType(testUserTypeDTO2);
//        List<UserTypeDTO> userTypeDTOList = userTypeServiceImpl.findAllUserTypeDTOs();
//        assertEquals(userTypeDTOList.size(), 2);
//
//    }
//
//    @Test
//    void deleteShouldDeleteUserTypeByTypeUserType(){
//        userTypeServiceImpl.createUserType(testUserTypeDTO);
//        userTypeServiceImpl.createUserType(testUserTypeDTO2);
//        userTypeServiceImpl.deleteUserTypeByType(testUserTypeDTO.getName());
//        assertThat(userTypeRepository.findUserTypeByNameLikeIgnoreCase(testUserTypeDTO.getName())).isEmpty();
//    }
//
//    @Test
//    void deleteAllShouldDeleteUserTypeByTypeAllUserTypes(){
//        userTypeServiceImpl.createUserType(testUserTypeDTO);
//        userTypeServiceImpl.createUserType(testUserTypeDTO2);
//        userTypeServiceImpl.deleteAllUserTypes();
//        assertTrue(userTypeRepository.findAll().isEmpty(),"не удалились все данные из списка user_types");
//    }
//
//
//}