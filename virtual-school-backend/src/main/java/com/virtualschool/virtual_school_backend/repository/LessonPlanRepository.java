package com.virtualschool.virtual_school_backend.repository;

import com.virtualschool.virtual_school_backend.model.LessonPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonPlanRepository extends JpaRepository<LessonPlan, Long> {
    List<LessonPlan> findBySubjectId(Long subjectId);
    List<LessonPlan> findByStudentGroupId(Long studentGroupId);
    List<LessonPlan> findByUserId(Long userId);
}
