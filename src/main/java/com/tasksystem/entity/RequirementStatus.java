package com.tasksystem.entity;

public enum RequirementStatus {
    DRAFT("草稿"),
    REVIEW("審核中"),
    APPROVED("已核准"),
    REJECTED("已拒絕");

    private final String description;

    RequirementStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
