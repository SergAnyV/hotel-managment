package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.jobtypedto.JobTypeDTO;
import com.asv.hotel.dto.jobtypedto.JobTypeSimpleDTO;
import com.asv.hotel.dto.mapper.JobTypeMapper;
import com.asv.hotel.dto.mapper.UserTypeMapper;
import com.asv.hotel.dto.usertypedto.UserTypeDTO;
import com.asv.hotel.entities.JobType;
import com.asv.hotel.entities.UserType;
import com.asv.hotel.exceptions.HotelDataAlreadyExistsException;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.repositories.JobTypeRepository;
import com.asv.hotel.services.JobTypeInternalService;

import com.asv.hotel.services.UserTypeInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobTypeServiceImpl implements JobTypeInternalService {
    private final JobTypeRepository jobTypeRepository;
    private final UserTypeInternalService userTypeInternalService;

    @Transactional
    @Override
    public List<JobTypeDTO> findAll() {

        List<JobType> jobTypeList = jobTypeRepository.findAll();
        jobTypeList.forEach(jobType -> Hibernate.initialize(jobType.getUserTypes()));

        return jobTypeList.stream()
                .map(jp -> JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jp))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public JobTypeDTO createJobType(JobTypeSimpleDTO jobTypeSimpleDTO) {
        JobType jobType = JobTypeMapper.INSTANCE.jobTypeSimpleDTOToJobType(jobTypeSimpleDTO);
        jobType.setIsActive(true);
        try {
            jobType = jobTypeRepository.save(jobType);
            JobTypeDTO result = JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jobType);
            return result;
        } catch (DataIntegrityViolationException ex) {
            log.error("Error: не пройдена проверка на уникальность полей jobType при сохранении в методе createJobType"
                    + "jobTypeSimpleDTO ={}", jobTypeSimpleDTO, ex);
            throw new HotelDataAlreadyExistsException(String.format(" Fileds is not unique for this jobTypeSimpleDTO= '%s' ",
                    jobTypeSimpleDTO));
        }
    }

    @Transactional
    @Override
    public List<JobTypeDTO> findJobTypesDTOByTitle(String title) {
        title = cleanString(title);
        List<JobType> listJT = jobTypeRepository.findJobTypesByTitleIgnoreCase(title);

        return listJT.stream()
                .map(jobType -> JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jobType))
                .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public List<JobType> findJobTypesByTitle(String title) {
        title = cleanString(title);

        List<JobType> listJT = jobTypeRepository.findJobTypesByTitleIgnoreCase(title);
        return listJT;

    }

    @Transactional
    @Override
    public List<JobTypeDTO> findActiveJobTypesDTOByTitle(String title) {
        title = cleanString(title);

        List<JobType> listJT = jobTypeRepository.findActiveJobTypesByTitleIgnoreCase(title);
        return listJT.stream()
                .map(jobType -> JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jobType))
                .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public List<JobType> findActiveJobTypesByTitle(String title) {
        title = cleanString(title);

        List<JobType> listJT = jobTypeRepository.findActiveJobTypesByTitleIgnoreCase(title);
        return listJT;

    }

    @Transactional
    @Override
    public List<JobTypeDTO> findJobTypesDTOByTitleAndActiveStatusWithoutUserType(String title, String isActive) {
        isActive = cleanString(isActive);
        title = cleanString(title);
        Boolean isActiveBoolean = Boolean.valueOf(isActive);

        List<JobType> listJT = jobTypeRepository.findJobTypesByTitleAndActiveStatusWithoutUserType(title, isActiveBoolean);
        return listJT.stream()
                .map(jt -> JobTypeMapper.INSTANCE.jobTypeToJobTypeDTO(jt))
                .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public List<JobType> findJobTypesByTitleAndActiveStatusWithoutUserType(String title, String isActive) {
        Boolean isActiveBoolean = Boolean.valueOf(isActive);
        title = cleanString(title);
        List<JobType> listJT = jobTypeRepository.findJobTypesByTitleAndActiveStatusWithoutUserType(title, isActiveBoolean);
        return listJT;

    }

    @Transactional
    @Override
    public void deleteJobTypeById(Long id) {
        if (jobTypeRepository.deleteJobTypeById(id) == 0) {
            log.error("Error: не существует jobtype с id= {} для удаления с помощью deleteJobTypeById", id);
            throw new HotelDataNotFoundException("There is no JobType for delete with this id =" + id);
        }

    }

    @Transactional
    public void deleteJobTypeByTitle(String title) {
        if (jobTypeRepository.deleteJobTypeByTitleIgnoreCase(title) == 0) {
            log.error("Error: не существует jobtype с title= {}  для удаления с помощью deleteJobTypeByTitle", title);
            throw new HotelDataNotFoundException("There is no JobType for delete with this title =" + title);
        }
    }

    @Transactional
    public void updateJobTypesDescriptionAndActiveStatusByTitle(String title, String description, String status) {
        Boolean statusB = Boolean.valueOf(status);
        if (jobTypeRepository.updateJobTypesDescriptionAndActiveStatusByTitle(title, description, statusB) == 0) {
            throw new HotelDataNotFoundException("Problem with updating JobType");
        }
    }

    @Transactional
    public Set<UserTypeDTO> addUserTypeToJobType(String jobTypeTitle, String userTypeRole) {

        jobTypeTitle = cleanString(jobTypeTitle);
        userTypeRole = cleanString(userTypeRole);

        JobType jobType = jobTypeRepository.findJobTypesByTitleIgnoreCase(jobTypeTitle).get(0);
        Set<UserType> userTypeSet = jobType.getUserTypes();
        UserType userType = userTypeInternalService.findActiveUserTypeByType(userTypeRole);
        userTypeSet.add(userType);
        jobType = jobTypeRepository.save(jobType);
        return jobType.getUserTypes().stream().map(ut -> UserTypeMapper.INSTANCE.userTypeToUserTypeDTO(ut))
                .collect(Collectors.toSet());
    }


    private String cleanString(String line) {
        return line.trim().toLowerCase();
    }
    // TODO : переделать для админа и менеджера для изменений см. репорт сервисы
}
