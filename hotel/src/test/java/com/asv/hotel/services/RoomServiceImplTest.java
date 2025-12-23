//package com.asv.hotel.services;
//
//import com.asv.hotel.dto.roomdto.RoomDTO;
//import com.asv.hotel.entities.enums.RoomType;
//import com.asv.hotel.exceptions.HotelDataAlreadyExistsException;
//import com.asv.hotel.repositories.RoomRepository;
//import com.asv.hotel.services.implementations.RoomServiceImpl;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestConstructor;
//
//import javax.sql.DataSource;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@SpringBootTest
//@ActiveProfiles("test")
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//class RoomServiceImplTest {
//
//    private DataSource dataSource;
//    private RoomServiceImpl roomServiceImpl;
//    private RoomRepository roomRepository;
//    private RoomDTO testRoomDTO;
//
//    public RoomServiceImplTest(RoomServiceImpl roomServiceImpl, RoomRepository roomRepository, DataSource dataSource) {
//        this.roomServiceImpl = roomServiceImpl;
//        this.roomRepository = roomRepository;
//        this.dataSource = dataSource;
//    }
//
//    @BeforeEach
//    void before() {
//        testRoomDTO = RoomDTO.builder()
//                .number("101")
//                .type(RoomType.ECONOM)
//                .description("Test room")
//                .capacity(2)
//                .pricePerNight(BigDecimal.valueOf(1000))
//                .isAvailable(true)
//                .build();
//    }
//
//    @AfterEach
//    void after(){
//        try {
//            roomRepository.deleteAll();
//        }catch (Exception e){
//          e.printStackTrace();
//        }
//    }
//
//// проверка соединения с базой
//    @Test
//    void testDataSourceIsNotNull() {
//        assertThat(dataSource).isNotNull();
//    }
//
//    //сохранение новой комнаты в репозитории CREATE
//    @Test
//    void createRoomRoomShouldBeSavedRoom() {
//        RoomDTO roomDTO = roomServiceImpl.createRoom(testRoomDTO);
//        assertEquals(roomDTO.getNumber(), testRoomDTO.getNumber());
//        assertEquals(roomDTO.getType(),testRoomDTO.getType());
//        assertEquals(roomDTO.getPricePerNight(),testRoomDTO.getPricePerNight());
//        assertEquals(roomDTO.getIsAvailable(),testRoomDTO.getIsAvailable());
//    }
//
//    @Test
//    void createRoomRoom_ShouldThrowIfRoomExists() {
//        roomServiceImpl.createRoom(testRoomDTO);
//
//        assertThatThrownBy(() -> roomServiceImpl.createRoom(testRoomDTO))
//                .isInstanceOf(HotelDataAlreadyExistsException.class)
//                .hasMessageContaining(testRoomDTO.getNumber());
//    }
//    // READ чтение из базф
//    @Test
//    void findAll_ShouldReturnAllRoomsRooms() {
//        roomServiceImpl.createRoom(testRoomDTO);
//        RoomDTO secondRoomDTO= RoomDTO.builder()
//                .number("102")
//                .type(RoomType.DELUXE)
//                .description("Test room2")
//                .capacity(2)
//                .pricePerNight(BigDecimal.valueOf(1000))
//                .isAvailable(true)
//                .build();
//        roomServiceImpl.createRoom(secondRoomDTO);
//        List<RoomDTO> rooms = roomServiceImpl.findAllRoomsDTO();
//
//        assertEquals(rooms.size(),2);
//    }
//
//    @Test
//    void findRoomDTOByNumber_ShouldReturnRoom() {
//        roomServiceImpl.createRoom(testRoomDTO);
//        RoomDTO foundRoom = roomServiceImpl.findRoomDTOByNumber("101");
//
//        assertThat(foundRoom).isNotNull();
//        assertThat(foundRoom.getNumber()).isEqualTo("101");
//    }
//
//    @Test
//    void findRoomDTOByNumber_ShouldThrowIfNotFound() {
//        assertThatThrownBy(() -> roomServiceImpl.findRoomDTOByNumber("999"));
//    }
//    // UPDATE обновление
//    @Test
//    void updateRoom_ShouldChangeDataRoomExistingRoom() {
//        RoomDTO savedRoom = roomServiceImpl.createRoom(testRoomDTO);
//        savedRoom.setType(RoomType.DELUXE);
//        savedRoom.setPricePerNight(BigDecimal.valueOf(1500));
//
//        RoomDTO updatedRoom = roomServiceImpl.changeDataRoom(savedRoom);
//
//        assertThat(updatedRoom.getType()).isEqualTo(RoomType.DELUXE);
//        assertThat(updatedRoom.getPricePerNight()).isEqualByComparingTo("1500");
//    }
//
//    // DELETE
//    @Test
//    void deleteRoomByNumberRoom_ShouldRemoveRoom() {
//        roomServiceImpl.createRoom(testRoomDTO);
//        roomServiceImpl.deleteRoomByNumber("101");
//
//        assertThat(roomRepository.findRoomByNumberLikeIgnoreCase("101")).isEmpty();
//    }
//}