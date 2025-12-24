//package com.asv.hotel.services.implementations;
//
//import com.asv.hotel.dto.jobtypedto.JobTypeDTO;
//import com.asv.hotel.dto.jobtypedto.JobTypeSimpleDTO;
//import com.asv.hotel.dto.usertypedto.UserTypeDTO;
//import com.asv.hotel.entities.JobType;
//import com.asv.hotel.entities.UserType;
//import com.asv.hotel.repositories.JobTypeRepository;
//import com.asv.hotel.services.JobTypeInternalService;
//import com.asv.hotel.services.UserTypeInternalService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestConstructor;
//
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//@SpringBootTest
//@ActiveProfiles("test")
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//class JobTypeServiceImplTest {
//    private JobTypeDTO jobTypeDTO,jobTypeDTO2;
//    private JobType jobType,jobType2;
//    private JobTypeSimpleDTO jobTypeSimpleDTO,jobTypeSimpleDTO2;
//    private JobTypeInternalService jobTypeInternalService;
//    private UserTypeInternalService userTypeInternalService;
//    private UserTypeDTO userTypeDTO,userTypeDTO2;
//    private UserType userType,userType2;
//    private JobTypeRepository jobTypeRepository;
//
//    public JobTypeServiceImplTest(JobTypeInternalService jobTypeInternalService, UserTypeInternalService userTypeInternalService, JobTypeRepository jobTypeRepository) {
//        this.jobTypeInternalService = jobTypeInternalService;
//        this.userTypeInternalService = userTypeInternalService;
//        this.jobTypeRepository = jobTypeRepository;
//    }
//
//    @BeforeEach
//    void setUp() {
//        jobTypeSimpleDTO=JobTypeSimpleDTO.builder().title("First job").description("Performing 1 job").build();
//        jobTypeSimpleDTO2=JobTypeSimpleDTO.builder().title("Second job").description("Performing 2 job").build();
//        jobTypeDTO =JobTypeDTO.builder().title("1JobDTO").description("Perform 1JDTO").isActive(true).build();
//        jobTypeDTO =JobTypeDTO.builder().title("2JobDTO").description("Perform 2JDTO").isActive(false).build();
//        jobType=JobType.builder().title("JobType1").description("JobType1 description").isActive(true).build();
//        jobType2=JobType.builder().title("JobType2").description("JobType2 description").isActive(true).build();
//
//    }
//
//
//    @Test
//    void findAll() {
//
//        System.out.println(jobTypeInternalService.findAll());
//    }
//
//
//    @Test
//    void createJobType() {
//        JobTypeDTO jobTypeDTOcreated= jobTypeInternalService.createJobType(jobTypeSimpleDTO);
//        assertNotNull(jobTypeDTOcreated);
//        assertEquals(jobTypeDTOcreated.getTitle(),jobTypeSimpleDTO.getTitle());
//    }
//
//    @Test
//    void findJobTypesDTOByTitle() {
//    }
//
//    @Test
//    void findJobTypesByTitle() {
//    }
//
//    @Test
//    void findActiveJobTypesDTOByTitle() {
//    }
//
//    @Test
//    void findActiveJobTypesByTitle() {
//    }
//
//    @Test
//    void findJobTypesDTOByTitleAndActiveStatusWithoutUserType() {
//    }
//
//    @Test
//    void findJobTypesByTitleAndActiveStatusWithoutUserType() {
//    }
//
//    @Test
//    void deleteJobTypeById() {
//    }
//
//    @Test
//    void deleteJobTypeByTitle() {
//    }
//
//    @Test
//    void updateJobTypesDescriptionAndActiveStatusByTitle() {
//    }
//
//    @Test
//    void addUserTypeToJobType() {
//    }
//}