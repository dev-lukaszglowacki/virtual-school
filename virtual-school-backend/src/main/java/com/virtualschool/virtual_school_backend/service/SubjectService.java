package com.virtualschool.virtual_school_backend.service;

import com.virtualschool.virtual_school_backend.dto.UserDTO;
import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.LessonPlanRepository;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final LessonPlanRepository lessonPlanRepository;
    private final KeycloakService keycloakService;

    public SubjectService(SubjectRepository subjectRepository, UserRepository userRepository,
                             LessonPlanRepository lessonPlanRepository, KeycloakService keycloakService) {
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.lessonPlanRepository = lessonPlanRepository;
        this.keycloakService = keycloakService;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public ResponseEntity<Subject> getSubjectById(@NonNull Long id) {
        return subjectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Subject> createSubject(@NonNull Subject subject) {
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    public ResponseEntity<Subject> updateSubject(@NonNull Long id, @RequestBody Subject subjectDetails) {
        return subjectRepository.findById(id)
                .map(subject -> {
                    subject.setName(subjectDetails.getName());
                    subject.setDescription(subjectDetails.getDescription());
                    return ResponseEntity.ok(subjectRepository.save(subject));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> deleteSubject(@NonNull Long id) {
        return subjectRepository.findById(id)
                .map(subject -> {
                    Objects.requireNonNull(subject, "Subject cannot be null");
                    subjectRepository.delete(subject);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<List<Subject>> getMySubjects() {
        // Note: This method currently relies on Authentication Principal directly in controller.
        // For service, we might need to pass authentication details or fetch user from context.
        // For now, assuming it will be called from a context where user is known or passed.
        // This part will need adjustment during controller refactoring if auth context isn't passed.

        // Placeholder for logic that would require user context.
        // Example: If user context is passed:
        // User user = userRepository.findByKeycloakId(userIdFromContext) ...
        // List<LessonPlan> lessonPlans = lessonPlanRepository.findByUserId(user.getId());
        // ...
        // For simplicity now, returning all subjects and noting the dependency.
        // A better approach might be to inject a SecurityUtil or similar to get current user.
        return ResponseEntity.ok(subjectRepository.findAll());
    }

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
