package com.asv.hotel.repositories;

import com.asv.hotel.entities.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobTypeRepository extends JpaRepository<JobType,Long> {


    List<JobType> findAll();

    @Query(value = "SELECT * FROM job_types WHERE id = :id",nativeQuery = true)
    Optional<JobType> findJobTypeById(@Param("id") Long id);

    @Query(value = "SELECT * FROM job_types WHERE title ILIKE :title" , nativeQuery = true)
    List<JobType> findJobTypesByTitleIgnoreCase(@Param("title") String title);

    @Query(value = "SELECT * FROM job_types WHERE title ILIKE :title AND is_active = true" , nativeQuery = true)
    List<JobType> findActiveJobTypesByTitleIgnoreCase(@Param("title") String title);

    @Query(value = "SELECT * FROM job_types WHERE title ILIKE :title AND is_active = :isactive" , nativeQuery = true)
    List<JobType> findJobTypesByTitleAndActiveStatusWithoutUserType(@Param("title") String title,
                                                                    @Param("isactive")Boolean isactive);

    @Modifying
    @Query(value = "DELETE FROM job_types WHERE id = :id",nativeQuery = true)
    int deleteJobTypeById(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM job_types WHERE title ILIKE :title",nativeQuery = true)
    int deleteJobTypeByTitleIgnoreCase(@Param("title") String title);

    @Modifying
    @Query(value = "UPDATE job_types SET description = :description, is_active = :isactive WHERE title = :title",nativeQuery = true)
    int updateJobTypesDescriptionAndActiveStatusByTitle(@Param("title")String title,
                                                        @Param("description") String description,
                                                        @Param("isactive") Boolean isactive);


}
