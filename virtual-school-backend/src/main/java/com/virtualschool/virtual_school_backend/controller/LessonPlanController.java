package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.repository.LessonPlanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-plans")
public class LessonPlanController {

    private final LessonPlanRepository lessonPlanRepository;

    public LessonPlanController(LessonPlanRepository lessonPlanRepository) {
        this.lessonPlanRepository = lessonPlanRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public List<LessonPlan> getAllLessonPlans() {
        return lessonPlanRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public LessonPlan createLessonPlan(@RequestBody LessonPlan lessonPlan) {
        return lessonPlanRepository.save(lessonPlan);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<LessonPlan> updateLessonPlan(@PathVariable Long id, @RequestBody LessonPlan lessonPlanDetails) {
        return lessonPlanRepository.findById(id)
                .map(lessonPlan -> {
                    lessonPlan.setSubject(lessonPlanDetails.getSubject());
                    lessonPlan.setLecturer(lessonPlanDetails.getLecturer());
                    lessonPlan.setStudentGroup(lessonPlanDetails.getStudentGroup());
                    lessonPlan.setDayOfWeek(lessonPlanDetails.getDayOfWeek());
                    lessonPlan.setStartTime(lessonPlanDetails.getStartTime());
                    lessonPlan.setEndTime(lessonPlanDetails.getEndTime());
                    return ResponseEntity.ok(lessonPlanRepository.save(lessonPlan));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteLessonPlan(@PathVariable Long id) {
        return lessonPlanRepository.findById(id)
                .map(lessonPlan -> {
                    lessonPlanRepository.delete(lessonPlan);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
