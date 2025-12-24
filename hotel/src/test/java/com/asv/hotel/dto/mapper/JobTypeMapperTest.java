//package com.asv.hotel.dto.mapper;
//
//import com.asv.hotel.dto.jobtypedto.JobTypeDTO;
//import com.asv.hotel.dto.jobtypedto.JobTypeSimpleDTO;
//import com.asv.hotel.entities.JobType;
//import com.asv.hotel.entities.UserType;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JobTypeMapperTest {
//    private JobTypeMapper jobTypeMapper;
//    private JobType jobType;
//    private JobTypeDTO jobTypeDTO;
//    private JobTypeSimpleDTO jobTypeSimpleDTO;
//
//    @BeforeEach
//    void setUp() {
//        jobTypeMapper = JobTypeMapper.INSTANCE;
//
//        Set<UserType> userTypes = new HashSet<>();
//
//        UserType userType1 = UserType.builder()
//                .id(1L)
//                .name("ADMIN")
//                .description("Administrator role")
//                .isActive(true)
//                .jobTypeList(new HashSet<>())
//                .build();
//
//        UserType userType2 = UserType.builder()
//                .id(2L)
//                .name("MANAGER")
//                .description("Manager role")
//                .isActive(true)
//                .jobTypeList(new HashSet<>())
//                .build();
//
//        userTypes.add(userType1);
//        userTypes.add(userType2);
//
//
//        jobType = JobType.builder()
//                .id(1L)
//                .title("Кто нибудь")
//                .description("Делает что-то")
//                .isActive(true)
//                .createdAt(LocalDateTime.of(2023, 1, 1, 10, 0))
//                .updatedAt(LocalDateTime.of(2023, 1, 2, 10, 0))
//                .userTypes(userTypes)
//                .build();
//
//
//        jobTypeDTO = JobTypeDTO.builder()
//                .title("Кто нибудь")
//                .description("делает что-то и еще вот")
//                .isActive(true)
//                .userTypes(userTypes)
//                .build();
//
//
//        jobTypeSimpleDTO = JobTypeSimpleDTO.builder()
//                .title("Кто нибудь")
//                .description("делает что-то и еще вот")
//                .build();
//    }
//
//    @Test
//    void testJobTypeToJobTypeDTO() {
//
//        JobTypeDTO result = jobTypeMapper.jobTypeToJobTypeDTO(jobType);
//
//
//        assertNotNull(result);
//        assertEquals(jobType.getTitle(), result.getTitle());
//        assertEquals(jobType.getDescription(), result.getDescription());
//        assertEquals(jobType.getIsActive(), result.getIsActive());
//        assertEquals(jobType.getUserTypes().size(), result.getUserTypes().size());
//        assertTrue(result.getUserTypes().containsAll(jobType.getUserTypes()));
//    }
//
//    @Test
//    void testJobTypeDTOToJobType() {
//
//        JobType result = jobTypeMapper.jobTypeDTOToJobtype(jobTypeDTO);
//
//
//        assertNotNull(result);
//        assertEquals(jobTypeDTO.getTitle(), result.getTitle());
//        assertEquals(jobTypeDTO.getDescription(), result.getDescription());
//        assertEquals(jobTypeDTO.getIsActive(), result.getIsActive());
//        assertEquals(jobTypeDTO.getUserTypes().size(), result.getUserTypes().size());
//        assertTrue(result.getUserTypes().containsAll(jobTypeDTO.getUserTypes()));
//    }
//
//    @Test
//    void testJobTypeDTOToJobTypeSimpleDTO() {
//
//        JobTypeSimpleDTO result = jobTypeMapper.jobTypeDTOToJobTypeSimpleDTO(jobTypeDTO);
//
//
//        assertNotNull(result);
//        assertEquals(jobTypeDTO.getTitle(), result.getTitle());
//        assertEquals(jobTypeDTO.getDescription(), result.getDescription());
//    }
//
//    @Test
//    void testJobTypeSimpleDTOToJobTypeDTO() {
//
//        JobTypeDTO result = jobTypeMapper.jobTypeSimpleDTOToJobTypeDTO(jobTypeSimpleDTO);
//
//        assertNotNull(result);
//        assertEquals(jobTypeSimpleDTO.getTitle(), result.getTitle());
//        assertEquals(jobTypeSimpleDTO.getDescription(), result.getDescription());
//        assertNull(result.getIsActive());
//        assertNull(result.getUserTypes());
//    }
//
//    @Test
//    void testJobTypeToJobTypeDTO_WithNullUserTypes() {
//
//        jobType.setUserTypes(null);
//
//        JobTypeDTO result = jobTypeMapper.jobTypeToJobTypeDTO(jobType);
//
//
//        assertNotNull(result);
//        assertEquals(jobType.getTitle(), result.getTitle());
//        assertNull(result.getUserTypes());
//    }
//
//    @Test
//    void testJobTypeDTOToJobType_WithNullUserTypes() {
//
//        jobTypeDTO.setUserTypes(null);
//
//
//        JobType result = jobTypeMapper.jobTypeDTOToJobtype(jobTypeDTO);
//
//
//        assertNotNull(result);
//        assertEquals(jobTypeDTO.getTitle(), result.getTitle());
//        assertNull(result.getUserTypes());
//    }
//
//    @Test
//    void testJobTypeToJobTypeDTO_WithEmptyUserTypes() {
//
//        jobType.setUserTypes(new HashSet<>());
//
//
//        JobTypeDTO result = jobTypeMapper.jobTypeToJobTypeDTO(jobType);
//
//        assertNotNull(result);
//        assertTrue(result.getUserTypes().isEmpty());
//    }
//
//    @Test
//    void testJobTypeDTOToJobType_WithEmptyUserTypes() {
//
//        jobTypeDTO.setUserTypes(new HashSet<>());
//
//
//        JobType result = jobTypeMapper.jobTypeDTOToJobtype(jobTypeDTO);
//
//
//        assertNotNull(result);
//        assertTrue(result.getUserTypes().isEmpty());
//    }
//
//    @Test
//    void testJobTypeToJobTypeDTO_WithNull() {
//
//        JobTypeDTO result = jobTypeMapper.jobTypeToJobTypeDTO(null);
//
//        assertNull(result);
//    }
//
//    @Test
//    void testJobTypeDTOToJobType_WithNull() {
//
//        JobType result = jobTypeMapper.jobTypeDTOToJobtype(null);
//
//
//        assertNull(result);
//    }
//
//    @Test
//    void testJobTypeDTOToJobTypeSimpleDTO_WithNull() {
//
//        JobTypeSimpleDTO result = jobTypeMapper.jobTypeDTOToJobTypeSimpleDTO(null);
//
//
//        assertNull(result);
//    }
//
//    @Test
//    void testJobTypeSimpleDTOToJobTypeDTO_WithNull() {
//
//        JobTypeDTO result = jobTypeMapper.jobTypeSimpleDTOToJobTypeDTO(null);
//
//
//        assertNull(result);
//    }
//
//    @Test
//    void testJobTypeToJobTypeDTO_WithInactiveStatus() {
//
//        jobType.setIsActive(false);
//
//
//        JobTypeDTO result = jobTypeMapper.jobTypeToJobTypeDTO(jobType);
//
//        assertNotNull(result);
//        assertFalse(result.getIsActive());
//    }
//
//    @Test
//    void testJobTypeDTOToJobType_WithInactiveStatus() {
//
//        jobTypeDTO.setIsActive(false);
//
//        JobType result = jobTypeMapper.jobTypeDTOToJobtype(jobTypeDTO);
//
//        assertNotNull(result);
//        assertFalse(result.getIsActive());
//    }
//
//    @Test
//    void testCircularMapping_JobTypeToDTOAndBack() {
//
//        JobTypeDTO dto = jobTypeMapper.jobTypeToJobTypeDTO(jobType);
//        JobType resultEntity = jobTypeMapper.jobTypeDTOToJobtype(dto);
//
//
//        assertNotNull(resultEntity);
//        assertEquals(jobType.getTitle(), resultEntity.getTitle());
//        assertEquals(jobType.getDescription(), resultEntity.getDescription());
//        assertEquals(jobType.getIsActive(), resultEntity.getIsActive());
//        assertEquals(jobType.getUserTypes().size(), resultEntity.getUserTypes().size());
//    }
//
//    @Test
//    void testCircularMapping_SimpleDTOToDTOAndBack() {
//
//        JobTypeDTO dto = jobTypeMapper.jobTypeSimpleDTOToJobTypeDTO(jobTypeSimpleDTO);
//        JobTypeSimpleDTO resultSimpleDTO = jobTypeMapper.jobTypeDTOToJobTypeSimpleDTO(dto);
//
//
//        assertNotNull(resultSimpleDTO);
//        assertEquals(jobTypeSimpleDTO.getTitle(), resultSimpleDTO.getTitle());
//        assertEquals(jobTypeSimpleDTO.getDescription(), resultSimpleDTO.getDescription());
//    }
//
//    @Test
//    void testMapping_WithSpecialCharacters() {
//
//        JobType specialJobType = JobType.builder()
//                .title("Разработчик 🚀")
//                .description("Описание с эмодзи и кириллицей")
//                .isActive(true)
//                .userTypes(new HashSet<>())
//                .build();
//
//
//        JobTypeDTO result = jobTypeMapper.jobTypeToJobTypeDTO(specialJobType);
//
//
//        assertNotNull(result);
//        assertEquals("Разработчик 🚀", result.getTitle());
//        assertEquals("Описание с эмодзи и кириллицей", result.getDescription());
//    }
//
//    @Test
//    void testMapping_WithLongValues() {
//
//        String longTitle = "A".repeat(50);
//        String longDescription = "B".repeat(250);
//
//        JobType longJobType = JobType.builder()
//                .title(longTitle)
//                .description(longDescription)
//                .isActive(true)
//                .userTypes(new HashSet<>())
//                .build();
//
//
//        JobTypeDTO result = jobTypeMapper.jobTypeToJobTypeDTO(longJobType);
//
//        assertNotNull(result);
//        assertEquals(longTitle, result.getTitle());
//        assertEquals(longDescription, result.getDescription());
//    }
//
//    @Test
//    void testJobTypeSimpleDTO_OnlyTitleAndDescriptionFields() {
//
//        JobTypeSimpleDTO result = jobTypeMapper.jobTypeDTOToJobTypeSimpleDTO(jobTypeDTO);
//
//
//        assertNotNull(result);
//
//        assertEquals(jobTypeDTO.getTitle(), result.getTitle());
//        assertEquals(jobTypeDTO.getDescription(), result.getDescription());
//
//    }
//
//    @Test
//    void testJobTypeToJobTypeSimpleDTO() {
//
//        JobTypeSimpleDTO result = jobTypeMapper.jobTypeToJobTypeSimpleDTO(jobType);
//
//
//        assertNotNull(result);
//        assertEquals(jobType.getTitle(), result.getTitle());
//        assertEquals(jobType.getDescription(), result.getDescription());
//
//    }
//
//}