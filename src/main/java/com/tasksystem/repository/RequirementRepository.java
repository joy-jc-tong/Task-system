package com.tasksystem.repository;

import com.tasksystem.entity.Requirement;
import com.tasksystem.entity.RequirementPriority;
import com.tasksystem.entity.RequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Long> {

    List<Requirement> findByStatus(RequirementStatus status);

    List<Requirement> findByPriority(RequirementPriority priority);

    List<Requirement> findByTitleContaining(String title);
}
