package com.example.tasksystem.repository;

import com.example.tasksystem.entity.Task;
import com.example.tasksystem.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任務資料訪問物件
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * 根據任務狀態查詢任務
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * 根據優先級查詢任務
     */
    List<Task> findByPriority(Integer priority);

    /**
     * 根據標題模糊查詢
     */
    List<Task> findByTitleContaining(String title);
}
