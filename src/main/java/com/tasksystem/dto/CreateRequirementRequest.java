package com.tasksystem.dto;

import com.tasksystem.entity.RequirementPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequirementRequest {

    @NotBlank(message = "需求標題不能為空")
    private String title;

    private String description;

    @NotNull(message = "優先級不能為空")
    private RequirementPriority priority;
}
