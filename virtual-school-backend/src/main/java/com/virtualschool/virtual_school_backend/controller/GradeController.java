package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.GradeDTO;
import com.virtualschool.virtual_school_backend.model.Grade;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.model.GradeValue;
import com.virtualschool.virtual_school_backend.repository.GradeRepository;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final KeycloakService keycloakService;

    public GradeController(GradeRepository gradeRepository, UserRepository userRepository,
                           SubjectRepository subjectRepository, KeycloakService keycloakService) {
        this.gradeRepository = gradeRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.keycloakService = keycloakService;
    }

    @PostMapping
    public ResponseEntity<GradeDTO> addGrade(@RequestBody GradeDTO gradeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakId = authentication.getName();

        User lecturer = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        User student = userRepository.findById(gradeDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Subject subject = subjectRepository.findById(gradeDTO.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Grade grade = new Grade(student, subject, lecturer, GradeValue.fromValue(gradeDTO.getGrade()));
        grade = gradeRepository.save(grade);

        return new ResponseEntity<>(toDTO(grade), HttpStatus.CREATED);
    }

    @GetMapping("/student")
    public ResponseEntity<List<GradeDTO>> getStudentGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakId = authentication.getName();

        User student = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Grade> grades = gradeRepository.findByStudentId(student.getId());
        return ResponseEntity.ok(grades.stream().map(this::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/lecturer")
    public ResponseEntity<List<GradeDTO>> getLecturerGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakId = authentication.getName();

        User lecturer = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        List<Grade> grades = gradeRepository.findByLecturerId(lecturer.getId());
        return ResponseEntity.ok(grades.stream().map(this::toDTO).collect(Collectors.toList()));
    }
    
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<GradeDTO>> getGradesForSubject(@PathVariable Long subjectId) {
        List<Grade> grades = gradeRepository.findBySubjectId(subjectId);
        return ResponseEntity.ok(grades.stream().map(this::toDTO).collect(Collectors.toList()));
    }

    private GradeDTO toDTO(Grade grade) {
        UserRepresentation studentUser = keycloakService.getUsersDetails(Collections.singletonList(grade.getStudent().getKeycloakId())).get(0);
        UserRepresentation lecturerUser = keycloakService.getUsersDetails(Collections.singletonList(grade.getLecturer().getKeycloakId())).get(0);

        return new GradeDTO(
                grade.getId(),
                grade.getStudent().getId(),
                studentUser.getFirstName() + " " + studentUser.getLastName(),
                grade.getSubject().getId(),
                grade.getSubject().getName(),
                grade.getLecturer().getId(),
                lecturerUser.getFirstName() + " " + lecturerUser.getLastName(),
                grade.getGrade().getValue()
        );
    }
}
