package com.tasksystem.dto;

import com.tasksystem.entity.RequirementPriority;
import com.tasksystem.entity.RequirementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequirementDTO {

    private Long id;

    private String title;

    private String description;

    private RequirementStatus status;

    private RequirementPriority priority;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
