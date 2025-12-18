//package com.asv.hotel.services.implementations;
//
//import com.asv.hotel.dto.bookingdto.BookingDTO;
//import com.asv.hotel.dto.bookingdto.BookingSimplDTO;
//import com.asv.hotel.dto.userdto.UserSimpleDTO;
//import com.asv.hotel.entities.UserType;
//import com.asv.hotel.repositories.BookingRepository;
//import com.asv.hotel.repositories.RoomRepository;
//import com.asv.hotel.repositories.UserRepository;
//import com.asv.hotel.repositories.UserTypeRepository;
//import com.asv.hotel.services.BookingService;
//import com.asv.hotel.services.EmailService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//class BookingServiceImplTest {
//
//    @Autowired
//    private BookingService bookingService;
//
//    @Autowired
//    private BookingRepository bookingRepository;
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserTypeRepository userTypeRepository;
//
//    @MockBean
//    private EmailService emailService;
//
//    private BookingSimplDTO validBooking;
//
//    @BeforeEach
//    void setUp() {
//
//        UserType userType = userTypeRepository.findUserTypeByNameLikeIgnoreCase("клиент")
//                .orElseThrow(() -> new RuntimeException("Тип 'клиент' не найден в тестовых данных"));
//
//
//        com.asv.hotel.entities.User user = new com.asv.hotel.entities.User();
//        user.setNickName("test_user_" + System.currentTimeMillis());
//        user.setFirstName("Иван");
//        user.setLastName("Иванов");
//        user.setFathersName("Иванович");
//        user.setEmail("test_" + System.currentTimeMillis() + "@example.com");
//        user.setPhoneNumber("79991234567");
//        user.setPassword("$2a$10$GjGv9uyQejDku0lPUkTLB.xti8VPuYMWwdx.vmY0mNXvd7TRLmyCi");
//        user.setType(userType);
//        user = userRepository.save(user);
//
//        UserSimpleDTO userSimpleDTO = UserSimpleDTO.builder()
//                .nickName(user.getNickName())
//                .firstName("Иван")
//                .lastName("Иванов")
//                .fathersName("Иванович")
//                .email(user.getEmail())
//                .phoneNumber("79991234567")
//                .build();
//
//        validBooking = BookingSimplDTO.builder()
//                .checkInDate(LocalDate.now().plusDays(1))
//                .checkOutDate(LocalDate.now().plusDays(3))
//                .persons(2)
//                .roomNumber("101")
//                .userSimpleDTO(userSimpleDTO)
//                .build();
//    }
//
//    @Test
//    void createBooking_shouldCreateAndReturnBookingDTO() {
//        BookingDTO result = bookingService.createBooking(validBooking);
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isNotNull();
//        assertThat(result.getTotalPrice()).isNotNull();
//        assertThat(result.getStatusOfBooking()).isEqualTo(com.asv.hotel.entities.enums.BookingStatus.CONFIRMED);
//    }
//
//    @Test
//    void deleteBookingById_shouldRemoveBooking() {
//        BookingDTO created = bookingService.createBooking(validBooking);
//        bookingService.deleteBookingById(created.getId());
//        assertThat(bookingRepository.findById(created.getId())).isEmpty();
//    }
//}