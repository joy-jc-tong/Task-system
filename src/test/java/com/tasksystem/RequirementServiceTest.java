package com.tasksystem;

import com.tasksystem.dto.CreateRequirementRequest;
import com.tasksystem.dto.RequirementDTO;
import com.tasksystem.entity.RequirementPriority;
import com.tasksystem.entity.RequirementStatus;
import com.tasksystem.repository.RequirementRepository;
import com.tasksystem.service.RequirementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RequirementServiceTest {

    @Autowired
    private RequirementService requirementService;

    @Autowired
    private RequirementRepository requirementRepository;

    @BeforeEach
    void setUp() {
        requirementRepository.deleteAll();
    }

    @Test
    void testCreateRequirement() {
        CreateRequirementRequest request = CreateRequirementRequest.builder()
                .title("測試需求")
                .description("測試描述")
                .priority(RequirementPriority.MEDIUM)
                .build();

        RequirementDTO created = requirementService.createRequirement(request);

        assertNotNull(created.getId());
        assertEquals("測試需求", created.getTitle());
        assertEquals(RequirementStatus.DRAFT, created.getStatus());
    }

    @Test
    void testGetAllRequirements() {
        CreateRequirementRequest request1 = CreateRequirementRequest.builder()
                .title("需求 1")
                .priority(RequirementPriority.LOW)
                .build();
        CreateRequirementRequest request2 = CreateRequirementRequest.builder()
                .title("需求 2")
                .priority(RequirementPriority.HIGH)
                .build();

        requirementService.createRequirement(request1);
        requirementService.createRequirement(request2);

        List<RequirementDTO> requirements = requirementService.getAllRequirements();
        assertEquals(2, requirements.size());
    }

    @Test
    void testUpdateRequirementStatus() {
        CreateRequirementRequest request = CreateRequirementRequest.builder()
                .title("測試需求")
                .priority(RequirementPriority.MEDIUM)
                .build();

        RequirementDTO created = requirementService.createRequirement(request);
        RequirementDTO updated = requirementService.updateRequirementStatus(created.getId(), RequirementStatus.REVIEW);

        assertEquals(RequirementStatus.REVIEW, updated.getStatus());
    }
}
