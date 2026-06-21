package com.tasksystem.service;

import com.tasksystem.dto.CreateRequirementRequest;
import com.tasksystem.dto.RequirementDTO;
import com.tasksystem.entity.RequirementStatus;

import java.util.List;

public interface RequirementService {

    RequirementDTO createRequirement(CreateRequirementRequest request);

    List<RequirementDTO> getAllRequirements();

    RequirementDTO getRequirementById(Long id);

    RequirementDTO updateRequirement(Long id, CreateRequirementRequest request);

    void deleteRequirement(Long id);

    List<RequirementDTO> getRequirementsByStatus(RequirementStatus status);

    RequirementDTO updateRequirementStatus(Long id, RequirementStatus status);
}
