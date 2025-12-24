//package com.asv.hotel.services;
//
//import com.asv.hotel.dto.promocodedto.PromoCodeDTO;
//import com.asv.hotel.entities.enums.TypeOfPromoCode;
//import com.asv.hotel.repositories.PromoCodeRepository;
//import com.asv.hotel.services.implementations.PromoCodeServiceImpl;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestConstructor;
//
//import javax.sql.DataSource;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//class PromoCodeServiceImplTest {
//    private DataSource dataSource;
//    private PromoCodeDTO testPromoCodeDTO;
//    private PromoCodeServiceImpl promoCodeServiceImpl;
//    private PromoCodeRepository promoCodeRepository;
//
//    public PromoCodeServiceImplTest(DataSource dataSource
//            , PromoCodeServiceImpl promoCodeServiceImpl
//            , PromoCodeRepository promoCodeRepository) {
//        this.dataSource = dataSource;
//        this.promoCodeServiceImpl = promoCodeServiceImpl;
//        this.promoCodeRepository = promoCodeRepository;
//    }
//
//    @BeforeEach
//    void setUp() {
//        LocalDate until=LocalDate.of(2024,10,23);
//        LocalDate from=LocalDate.of(2023,7,12);
//        BigDecimal discount=new BigDecimal(32);
//        testPromoCodeDTO =PromoCodeDTO.builder().code("some")
//                .typeOfPromoCode(TypeOfPromoCode.FIXED)
//                .isActive(true)
//                .validFromDate(from)
//                .validUntilDate(until)
//                .discountValue(discount).build();
//    }
//
//    @AfterEach
//    void tearDown() {
//        promoCodeRepository.deleteAll();
//    }
//
//    @Test
//    void createPromoCodeShouldBeSavedPromoCode() {
//        PromoCodeDTO savedPromoCode= promoCodeServiceImpl.createPromoCode(testPromoCodeDTO);
//        assertEquals(savedPromoCode.getCode(),testPromoCodeDTO.getCode());
//    }
//}