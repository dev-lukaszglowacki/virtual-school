package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.StudentDTO;
import com.virtualschool.virtual_school_backend.model.Student;
import com.virtualschool.virtual_school_backend.repository.StudentRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final KeycloakService keycloakService;

    public StudentController(StudentRepository studentRepository, KeycloakService keycloakService) {
        this.studentRepository = studentRepository;
        this.keycloakService = keycloakService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher')")
    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        List<String> keycloakIds = students.stream().map(Student::getKeycloakId).collect(Collectors.toList());
        
        Map<String, UserRepresentation> userMap = keycloakService.getUsersDetails(keycloakIds).stream()
                .collect(Collectors.toMap(UserRepresentation::getId, Function.identity()));

        return students.stream()
                .map(student -> new StudentDTO(student, userMap.get(student.getKeycloakId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('student')")
    @Transactional(readOnly = true)
    public ResponseEntity<StudentDTO> getMe(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return studentRepository.findByKeycloakId(keycloakId)
                .map(student -> {
                    UserRepresentation user = keycloakService.getUsersDetails(List.of(keycloakId)).get(0);
                    return ResponseEntity.ok(new StudentDTO(student, user));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
