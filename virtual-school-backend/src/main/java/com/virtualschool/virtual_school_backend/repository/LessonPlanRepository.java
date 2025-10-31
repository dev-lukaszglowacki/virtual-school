package com.virtualschool.virtual_school_backend.repository;

import com.virtualschool.virtual_school_backend.model.LessonPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonPlanRepository extends JpaRepository<LessonPlan, Long> {
}
