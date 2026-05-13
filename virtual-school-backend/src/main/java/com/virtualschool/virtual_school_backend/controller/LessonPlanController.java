package com.virtualschool.virtual_school_backend.controller;

import java.util.List;

import com.virtualschool.virtual_school_backend.dto.CreateLessonPlanDTO;
import com.virtualschool.virtual_school_backend.dto.LessonPlanDTO;
import com.virtualschool.virtual_school_backend.dto.UpdateLessonPlanDTO;
import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.service.LessonPlanService;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lesson-plans")
public class LessonPlanController {

    private final LessonPlanService lessonPlanService;

    public LessonPlanController(LessonPlanService lessonPlanService) {
        this.lessonPlanService = lessonPlanService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public List<LessonPlanDTO> getAllLessonPlans() {
        return lessonPlanService.getAllLessonPlans();
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<LessonPlan> createLessonPlan(@NonNull @RequestBody CreateLessonPlanDTO lessonPlanDetails) {
        return lessonPlanService.createLessonPlan(lessonPlanDetails);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<LessonPlan> updateLessonPlan(@NonNull @PathVariable Long id, @RequestBody UpdateLessonPlanDTO lessonPlanDetails) {
        return lessonPlanService.updateLessonPlan(id, lessonPlanDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteLessonPlan(@NonNull @PathVariable Long id) {
        return lessonPlanService.deleteLessonPlan(id);
    }
}
