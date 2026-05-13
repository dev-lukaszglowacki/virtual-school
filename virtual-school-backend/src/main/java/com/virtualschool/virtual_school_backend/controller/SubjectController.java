package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.dto.UserDTO;
import com.virtualschool.virtual_school_backend.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public ResponseEntity<Subject> getSubjectById(@NonNull @PathVariable Long id) {
        return subjectService.getSubjectById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Subject> createSubject(@NonNull @RequestBody Subject subject) {
        return subjectService.createSubject(subject);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Subject> updateSubject(@NonNull @PathVariable Long id, @RequestBody Subject subjectDetails) {
        return subjectService.updateSubject(id, subjectDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteSubject(@NonNull @PathVariable Long id) {
        return subjectService.deleteSubject(id);
    }

    @GetMapping("/my-subjects")
    @PreAuthorize("hasRole('teacher')")
    public ResponseEntity<List<Subject>> getMySubjects(Authentication authentication) {
        // Extract keycloakId from authentication and pass it to the service
        String keycloakId = authentication.getName();
        // NOTE: The SubjectService.getMySubjects() currently returns all subjects as a placeholder.
        // It needs to be updated to accept the keycloakId and fetch subjects based on the user's role and associated lesson plans.
        // For now, calling the placeholder method. A more robust implementation will be needed here.
        return subjectService.getMySubjects(); // Placeholder call
    }

    @GetMapping("/{id}/users")
    @PreAuthorize("hasRole('teacher')")
    public ResponseEntity<List<UserDTO>> getUsersForSubject(@PathVariable Long id) {
        return subjectService.getUsersForSubject(id);
    }
}
