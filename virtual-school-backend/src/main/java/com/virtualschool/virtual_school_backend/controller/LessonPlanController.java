package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.LessonPlanDTO;
import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.repository.LessonPlanRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lesson-plans")
public class LessonPlanController {

    private final LessonPlanRepository lessonPlanRepository;
    private final KeycloakService keycloakService;

    public LessonPlanController(LessonPlanRepository lessonPlanRepository, KeycloakService keycloakService) {
        this.lessonPlanRepository = lessonPlanRepository;
        this.keycloakService = keycloakService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public List<LessonPlanDTO> getAllLessonPlans() {
        return lessonPlanRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private LessonPlanDTO convertToDto(LessonPlan lessonPlan) {
        UserRepresentation lecturerUser = keycloakService.getUsersDetails(Collections.singletonList(lessonPlan.getLecturer().getKeycloakId())).get(0);
        String lecturerName = lecturerUser != null ? lecturerUser.getFirstName() + " " + lecturerUser.getLastName() : "N/A";

        return new LessonPlanDTO(
                lessonPlan.getId(),
                lessonPlan.getSubject().getName(),
                lecturerName,
                lessonPlan.getStudentGroup() != null ? lessonPlan.getStudentGroup().getName() : "N/A",
                lessonPlan.getDayOfWeek(),
                lessonPlan.getStartTime(),
                lessonPlan.getEndTime()
        );
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
