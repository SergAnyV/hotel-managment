package com.asv.hotel.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "job_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, unique = true, length = 50)
    private String title;

    @Column(name = "description", nullable = false, length = 250)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_type_job_type",
    joinColumns = @JoinColumn(name = "job_type_id"),
            inverseJoinColumns = @JoinColumn(name ="user_type_id")
    )
    private Set<UserType> userTypes=new HashSet<>();

}
