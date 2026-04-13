package com.tasksystem.entity;

/**
 * 任務狀態列舉
 */
public enum TaskStatus {
    TODO("待辦"),
    IN_PROGRESS("進行中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
