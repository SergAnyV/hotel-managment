package com.asv.models.jobtypedto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobType {

    private Long id;

    private String title;

    private String description;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Set<UserType> userTypes = new HashSet<>();

}
