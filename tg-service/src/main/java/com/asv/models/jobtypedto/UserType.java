package com.asv.models.jobtypedto;

import com.asv.models.enums.UserRole;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserType {

    private Long id;


    private String name;


    private String description;


    private UserRole role;


    private String roleDescription;


    private Boolean isActive;

    private Set<JobType> jobTypeList = new HashSet<>();
}
