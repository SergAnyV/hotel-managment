package com.asv.hotel.services;

import com.asv.hotel.dto.jobtypedto.JobTypeDTO;
import com.asv.hotel.dto.jobtypedto.JobTypeSimpleDTO;
import com.asv.hotel.dto.usertypedto.UserTypeDTO;
import com.asv.hotel.entities.JobType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


public interface JobTypeService {
    List<JobTypeDTO> findAll();
    JobTypeDTO createJobType(JobTypeSimpleDTO jobTypeSimpleDTO);
    List<JobTypeDTO> findJobTypesDTOByTitle(String title);
    List<JobTypeDTO> findActiveJobTypesDTOByTitle(String title);
    List<JobTypeDTO> findJobTypesDTOByTitleAndActiveStatusWithoutUserType(String title, String isActive);
    void deleteJobTypeByTitle(String tittle);
    Set<UserTypeDTO> addUserTypeToJobType(String jobTypeTitle, String userTypeRole);
}
