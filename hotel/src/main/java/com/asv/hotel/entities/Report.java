package com.asv.hotel.entities;

import com.asv.hotel.entities.enums.ReportStatus;
import com.asv.hotel.entities.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @Column(name = "description_status", length = 50)
    private String descriptionStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ReportType reportType;

    @Column(name = "description_type", length = 50)
    private String descriptionType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;

    @OneToMany(mappedBy = "report", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ReportAttachment> reportAttachmentSet=new HashSet<>();


    @PrePersist
    @PreUpdate
    private void preUpdate() {
        if (reportType != null) {
            this.descriptionType = reportType.getDescription();
        }
        if (reportStatus != null) {
            this.descriptionStatus = reportStatus.getDescription();
        }
    }

    public void addAttachment(ReportAttachment attachment){
        attachment.setReport(this);
        this.reportAttachmentSet.add(attachment);
    }

}
