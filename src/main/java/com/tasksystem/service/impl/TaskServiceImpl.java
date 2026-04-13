package com.tasksystem.service.impl;

import com.tasksystem.dto.CreateTaskRequest;
import com.tasksystem.dto.TaskDTO;
import com.tasksystem.entity.Task;
import com.tasksystem.entity.TaskStatus;
import com.tasksystem.repository.TaskRepository;
import com.tasksystem.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 任務業務邏輯實現類
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public TaskDTO createTask(CreateTaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TaskStatus.TODO)
                .build();
        
        Task savedTask = taskRepository.save(Objects.requireNonNull(task));
        return convertToDTO(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        Objects.requireNonNull(id, "id 不能為 null");
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 id 為 " + id + " 的任務"));
        return convertToDTO(task);
    }

    @Override
    public TaskDTO updateTask(Long id, CreateTaskRequest request) {
        Objects.requireNonNull(id, "id 不能為 null");
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 id 為 " + id + " 的任務"));
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        
        Task updatedTask = taskRepository.save(Objects.requireNonNull(task));
        return convertToDTO(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        Objects.requireNonNull(id, "id 不能為 null");
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("找不到 id 為 " + id + " 的任務");
        }
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO updateTaskStatus(Long id, TaskStatus status) {
        Objects.requireNonNull(id, "id 不能為 null");
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 id 為 " + id + " 的任務"));
        
        task.setStatus(status);
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        } else {
            task.setCompletedAt(null);
        }
        
        Task updatedTask = taskRepository.save(Objects.requireNonNull(task));
        return convertToDTO(updatedTask);
    }

    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }
}
