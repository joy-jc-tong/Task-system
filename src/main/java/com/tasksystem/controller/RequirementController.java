package com.tasksystem.controller;

import com.tasksystem.dto.CreateRequirementRequest;
import com.tasksystem.dto.RequirementDTO;
import com.tasksystem.entity.RequirementStatus;
import com.tasksystem.service.RequirementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requirements")
@RequiredArgsConstructor
public class RequirementController {

    private final RequirementService requirementService;

    @PostMapping
    public ResponseEntity<RequirementDTO> createRequirement(@Valid @RequestBody CreateRequirementRequest request) {
        RequirementDTO requirement = requirementService.createRequirement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(requirement);
    }

    @GetMapping
    public ResponseEntity<List<RequirementDTO>> getAllRequirements() {
        List<RequirementDTO> requirements = requirementService.getAllRequirements();
        return ResponseEntity.ok(requirements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequirementDTO> getRequirementById(@PathVariable Long id) {
        RequirementDTO requirement = requirementService.getRequirementById(id);
        return ResponseEntity.ok(requirement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequirementDTO> updateRequirement(
            @PathVariable Long id,
            @Valid @RequestBody CreateRequirementRequest request) {
        RequirementDTO requirement = requirementService.updateRequirement(id, request);
        return ResponseEntity.ok(requirement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequirement(@PathVariable Long id) {
        requirementService.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<RequirementDTO>> getRequirementsByStatus(
            @PathVariable RequirementStatus status) {
        List<RequirementDTO> requirements = requirementService.getRequirementsByStatus(status);
        return ResponseEntity.ok(requirements);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RequirementDTO> updateRequirementStatus(
            @PathVariable Long id,
            @RequestParam RequirementStatus status) {
        RequirementDTO requirement = requirementService.updateRequirementStatus(id, status);
        return ResponseEntity.ok(requirement);
    }
}
