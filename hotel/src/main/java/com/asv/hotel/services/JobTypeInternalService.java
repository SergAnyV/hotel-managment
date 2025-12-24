package com.asv.hotel.services;

import com.asv.hotel.entities.JobType;

import java.util.List;

public interface JobTypeInternalService extends JobTypeService {
    List<JobType> findJobTypesByTitle(String title);
    List<JobType> findActiveJobTypesByTitle(String title);
    List<JobType> findJobTypesByTitleAndActiveStatusWithoutUserType(String title, String isActive);
    void deleteJobTypeById(Long id);

}
