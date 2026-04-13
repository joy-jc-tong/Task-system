package com.tasksystem.entity;

/**
 * 任務狀態列舉
 */
public enum TaskStatus {
    PENDING("閒置"),
    RUNNING("進行中"),
    SUCCESS("成功"),
    FAILED("失敗");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
