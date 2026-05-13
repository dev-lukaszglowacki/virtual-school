package com.virtualschool.virtual_school_backend.service;

import com.virtualschool.virtual_school_backend.dto.GradeDTO;
import com.virtualschool.virtual_school_backend.model.Grade;
import com.virtualschool.virtual_school_backend.model.GradeValue;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.producer.GradeProducer;
import com.virtualschool.virtual_school_backend.repository.GradeRepository;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final KeycloakService keycloakService;
    private final GradeProducer gradeProducer;

    public GradeService(GradeRepository gradeRepository, UserRepository userRepository, SubjectRepository subjectRepository, KeycloakService keycloakService, GradeProducer gradeProducer) {
        this.gradeRepository = gradeRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.keycloakService = keycloakService;
        this.gradeProducer = gradeProducer;
    }


    public ResponseEntity<GradeDTO> addGrade(GradeDTO gradeDTO) {
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

        // Send notification to Kafka
        String notificationMessage = String.format(
                "{\"studentId\": %d, \"subjectName\": \"%s\", \"gradeValue\": \"%s\"}",
                student.getId(), subject.getName(), grade.getGrade().getValue()
        );
        gradeProducer.sendGradeNotification(notificationMessage);

        return new ResponseEntity<>(toDTO(grade), HttpStatus.CREATED);
    }

    public ResponseEntity<List<GradeDTO>> getStudentGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakId = authentication.getName();

        User student = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Grade> grades = gradeRepository.findByStudentId(student.getId());
        return ResponseEntity.ok(grades.stream().map(this::toDTO).collect(Collectors.toList()));
    }

    public ResponseEntity<List<GradeDTO>> getLecturerGrades() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakId = authentication.getName();

        User lecturer = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        List<Grade> grades = gradeRepository.findByLecturerId(lecturer.getId());
        return ResponseEntity.ok(grades.stream().map(this::toDTO).collect(Collectors.toList()));
    }

    public ResponseEntity<List<GradeDTO>> getGradesForSubject(Long subjectId) {
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
                grade.getGrade().getValue(),
                grade.getCreatedAt(),
                grade.getUpdatedAt()
        );
    }

}
