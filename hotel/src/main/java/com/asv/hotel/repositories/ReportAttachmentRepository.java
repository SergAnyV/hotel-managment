package com.asv.hotel.repositories;

import com.asv.hotel.entities.ReportAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportAttachmentRepository extends JpaRepository<ReportAttachment, Long> {

    @Override
    Optional<ReportAttachment> findById(Long id);

    @Query(value = """
            SELECT * FROM report_attachments 
            WHERE report_id=:id
            """,nativeQuery = true)
    List<ReportAttachment> findReportAttachmentByReportID(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM report_attachments WHERE id=:id",nativeQuery = true)
    int deleteReportAttachmentById(@Param("id") Long id);

    @Query(value = "SELECT * FROM report_attachments WHERE report_id =:report_id",nativeQuery = true)
    List<ReportAttachment> findReportAttachmentForZipListByReportId(@Param("report_id")Long reportId);
}
