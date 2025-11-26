package com.virtualschool.virtual_school_backend.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.virtualschool.virtual_school_backend.dto.CreateLessonPlanDTO;
import com.virtualschool.virtual_school_backend.dto.LessonPlanDTO;
import com.virtualschool.virtual_school_backend.dto.UpdateLessonPlanDTO;
import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.LessonPlanRepository;
import com.virtualschool.virtual_school_backend.repository.StudentGroupRepository;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
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

    private final LessonPlanRepository lessonPlanRepository;
    private final KeycloakService keycloakService;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final StudentGroupRepository studentGroupRepository;

    public LessonPlanController(LessonPlanRepository lessonPlanRepository,
                                KeycloakService keycloakService,
                                SubjectRepository subjectRepository,
                                UserRepository userRepository,
                                StudentGroupRepository studentGroupRepository) {
        this.lessonPlanRepository = lessonPlanRepository;
        this.keycloakService = keycloakService;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.studentGroupRepository = studentGroupRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public List<LessonPlanDTO> getAllLessonPlans() {
        return lessonPlanRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    private LessonPlanDTO convertToDto(LessonPlan lessonPlan) {
        String lecturerName = "N/A";
        if (lessonPlan.getUser() != null && lessonPlan.getUser().getKeycloakId() != null) {
            UserRepresentation lecturerUser = keycloakService.getUsersDetails(Collections.singletonList(lessonPlan.getUser().getKeycloakId())).get(0);
            if (lecturerUser != null) {
                lecturerName = lecturerUser.getFirstName() + " " + lecturerUser.getLastName();
            }
        }

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
    public LessonPlan createLessonPlan(@RequestBody CreateLessonPlanDTO lessonPlanDetails) {
        Subject subject = subjectRepository.findById(lessonPlanDetails.getSubjectId()).orElse(null);
        User user = userRepository.findById(lessonPlanDetails.getLecturerId()).orElse(null);
        StudentGroup studentGroup = studentGroupRepository.findById(lessonPlanDetails.getGroupId()).orElse(null);

        LessonPlan lessonPlan = new LessonPlan();
        lessonPlan.setSubject(subject);
        lessonPlan.setUser(user);
        lessonPlan.setStudentGroup(studentGroup);
        lessonPlan.setDayOfWeek(lessonPlanDetails.getDayOfWeek());
        lessonPlan.setStartTime(lessonPlanDetails.getStartTime());
        lessonPlan.setEndTime(lessonPlanDetails.getEndTime());

        return lessonPlanRepository.save(lessonPlan);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<LessonPlan> updateLessonPlan(@PathVariable Long id, @RequestBody UpdateLessonPlanDTO lessonPlanDetails) {
        return lessonPlanRepository.findById(id)
            .map(lessonPlan -> {
                Subject subject = subjectRepository.findById(lessonPlanDetails.getSubjectId()).orElse(null);
                User user = userRepository.findById(lessonPlanDetails.getLecturerId()).orElse(null);
                StudentGroup studentGroup = studentGroupRepository.findById(lessonPlanDetails.getGroupId()).orElse(null);

                lessonPlan.setSubject(subject);
                lessonPlan.setUser(user);
                lessonPlan.setStudentGroup(studentGroup);
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
