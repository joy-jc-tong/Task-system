package com.tasksystem.service.impl;

import com.tasksystem.dto.CreateRequirementRequest;
import com.tasksystem.dto.RequirementDTO;
import com.tasksystem.entity.Requirement;
import com.tasksystem.entity.RequirementStatus;
import com.tasksystem.repository.RequirementRepository;
import com.tasksystem.service.RequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RequirementServiceImpl implements RequirementService {

    private final RequirementRepository requirementRepository;

    @Override
    public RequirementDTO createRequirement(CreateRequirementRequest request) {
        Requirement requirement = Requirement.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(RequirementStatus.DRAFT)
                .build();

        Requirement saved = requirementRepository.save(Objects.requireNonNull(requirement));
        return convertToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequirementDTO> getAllRequirements() {
        return requirementRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RequirementDTO getRequirementById(Long id) {
        Objects.requireNonNull(id, "id 不能為 null");
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 id 為 " + id + " 的需求"));
        return convertToDTO(requirement);
    }

    @Override
    public RequirementDTO updateRequirement(Long id, CreateRequirementRequest request) {
        Objects.requireNonNull(id, "id 不能為 null");
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 id 為 " + id + " 的需求"));

        requirement.setTitle(request.getTitle());
        requirement.setDescription(request.getDescription());
        requirement.setPriority(request.getPriority());

        Requirement updated = requirementRepository.save(Objects.requireNonNull(requirement));
        return convertToDTO(updated);
    }

    @Override
    public void deleteRequirement(Long id) {
        Objects.requireNonNull(id, "id 不能為 null");
        if (!requirementRepository.existsById(id)) {
            throw new RuntimeException("找不到 id 為 " + id + " 的需求");
        }
        requirementRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequirementDTO> getRequirementsByStatus(RequirementStatus status) {
        return requirementRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RequirementDTO updateRequirementStatus(Long id, RequirementStatus status) {
        Objects.requireNonNull(id, "id 不能為 null");
        Requirement requirement = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 id 為 " + id + " 的需求"));

        requirement.setStatus(status);

        Requirement updated = requirementRepository.save(Objects.requireNonNull(requirement));
        return convertToDTO(updated);
    }

    private RequirementDTO convertToDTO(Requirement requirement) {
        return RequirementDTO.builder()
                .id(requirement.getId())
                .title(requirement.getTitle())
                .description(requirement.getDescription())
                .status(requirement.getStatus())
                .priority(requirement.getPriority())
                .createdAt(requirement.getCreatedAt())
                .updatedAt(requirement.getUpdatedAt())
                .build();
    }
}
