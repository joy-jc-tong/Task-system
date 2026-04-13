package com.tasksystem;

import com.tasksystem.dto.CreateTaskRequest;
import com.tasksystem.dto.TaskDTO;
import com.tasksystem.entity.TaskStatus;
import com.tasksystem.repository.TaskRepository;
import com.tasksystem.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任務服務單元測試
 */
@SpringBootTest
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void testCreateTask() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("測試任務")
                .description("測試描述")
                .priority(1)
                .build();

        TaskDTO createdTask = taskService.createTask(request);

        assertNotNull(createdTask.getId());
        assertEquals("測試任務", createdTask.getTitle());
        assertEquals(TaskStatus.TODO, createdTask.getStatus());
    }

    @Test
    void testGetAllTasks() {
        CreateTaskRequest request1 = CreateTaskRequest.builder()
                .title("任務 1")
                .priority(1)
                .build();
        CreateTaskRequest request2 = CreateTaskRequest.builder()
                .title("任務 2")
                .priority(2)
                .build();

        taskService.createTask(request1);
        taskService.createTask(request2);

        List<TaskDTO> tasks = taskService.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    void testUpdateTaskStatus() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("測試任務")
                .priority(1)
                .build();

        TaskDTO createdTask = taskService.createTask(request);
        TaskDTO updatedTask = taskService.updateTaskStatus(createdTask.getId(), TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }
}
