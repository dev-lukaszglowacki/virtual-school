package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.UserDTO;
import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import com.virtualschool.virtual_school_backend.repository.LessonPlanRepository;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final LessonPlanRepository lessonPlanRepository;
    private final KeycloakService keycloakService;

    public SubjectController(SubjectRepository subjectRepository, UserRepository userRepository,
                             LessonPlanRepository lessonPlanRepository, KeycloakService keycloakService) {
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.lessonPlanRepository = lessonPlanRepository;
        this.keycloakService = keycloakService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return subjectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectRepository.save(subject);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody Subject subjectDetails) {
        return subjectRepository.findById(id)
                .map(subject -> {
                    subject.setName(subjectDetails.getName());
                    subject.setDescription(subjectDetails.getDescription());
                    return ResponseEntity.ok(subjectRepository.save(subject));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        return subjectRepository.findById(id)
                .map(subject -> {
                    subjectRepository.delete(subject);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my-subjects")
    @PreAuthorize("hasRole('teacher')")
    public ResponseEntity<List<Subject>> getMySubjects(Authentication authentication) {
        String keycloakId = authentication.getName();
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found for Keycloak ID: " + keycloakId));

        List<LessonPlan> lessonPlans = lessonPlanRepository.findByUserId(user.getId());
        List<Subject> subjects = lessonPlans.stream()
                .map(LessonPlan::getSubject)
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}/users")
    @PreAuthorize("hasRole('teacher')")
    public ResponseEntity<List<UserDTO>> getUsersForSubject(@PathVariable Long id) {
        List<LessonPlan> lessonPlans = lessonPlanRepository.findBySubjectId(id);
        List<User> users = lessonPlans.stream()
                .flatMap(lessonPlan -> lessonPlan.getStudentGroup().getUsers().stream())
                .distinct()
                .collect(Collectors.toList());

        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserRepresentation userRepresentation = keycloakService.getUsersDetails(Collections.singletonList(user.getKeycloakId())).get(0);
                    return new UserDTO(user, userRepresentation);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }
}

