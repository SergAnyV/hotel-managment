package com.asv.hotel.repositories;

import com.asv.hotel.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {

    @Query(value ="SELECT * FROM reports WHERE id =:id",nativeQuery = true )
    Optional<Report> findReportById(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM report_attachments WHERE id =:reportAttachmentId AND report_id=:reportId",nativeQuery = true)
    int deleteReportAttachmentFromReportByID(@Param("reportId")Long reportId, @Param("reportAttachmentId")Long reportAttachmentId);
}
