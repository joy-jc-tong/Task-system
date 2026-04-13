package com.tasksystem.service;

import com.tasksystem.dto.CreateTaskRequest;
import com.tasksystem.dto.TaskDTO;
import com.tasksystem.entity.TaskStatus;

import java.util.List;

/**
 * 任務業務邏輯介面
 */
public interface TaskService {

    /**
     * 建立任務
     */
    TaskDTO createTask(CreateTaskRequest request);

    /**
     * 取得所有任務
     */
    List<TaskDTO> getAllTasks();

    /**
     * 取得單一任務
     */
    TaskDTO getTaskById(Long id);

    /**
     * 更新任務
     */
    TaskDTO updateTask(Long id, CreateTaskRequest request);

    /**
     * 刪除任務
     */
    void deleteTask(Long id);

    /**
     * 取得指定狀態的任務
     */
    List<TaskDTO> getTasksByStatus(TaskStatus status);

    /**
     * 更新任務狀態
     */
    TaskDTO updateTaskStatus(Long id, TaskStatus status);
}
