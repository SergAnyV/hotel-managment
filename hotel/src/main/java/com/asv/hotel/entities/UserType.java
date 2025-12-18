package com.asv.hotel.entities;

import com.asv.hotel.entities.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 30)
    private String name;

    @Column(name = "description", nullable = false, length = 250)
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role",nullable = false)
    private UserRole role;

    @Column(name = "role_description",nullable = false)
    private String roleDescription;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToMany(mappedBy = "userTypes", fetch = FetchType.LAZY)
    private Set<JobType> jobTypeList = new HashSet<>();

    @PrePersist
    @PreUpdate
    public void preUpdateRoleDescriptionAndNameToLowerCase(){
        this.roleDescription=role.getDescription();
        if (this.name!=null) {
            this.name = name.toLowerCase();
        }else this.name=null;
    }



}
