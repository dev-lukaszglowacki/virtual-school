package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.StudentDTO;
import com.virtualschool.virtual_school_backend.model.Lecturer;
import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.model.Student;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.repository.LecturerRepository;
import com.virtualschool.virtual_school_backend.repository.LessonPlanRepository;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectRepository subjectRepository;

    private final LecturerRepository lecturerRepository;

    private final LessonPlanRepository lessonPlanRepository;

    private final KeycloakService keycloakService;

    public SubjectController(SubjectRepository subjectRepository, LecturerRepository lecturerRepository,
        LessonPlanRepository lessonPlanRepository, KeycloakService keycloakService) {
        this.subjectRepository = subjectRepository;
        this.lecturerRepository = lecturerRepository;
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
    public ResponseEntity<List<Subject>> getMySubjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String keycloakId = authentication.getName();

        Lecturer lecturer = lecturerRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        return ResponseEntity.ok(subjectRepository.findByLecturerId(lecturer.getId()));
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasRole('teacher')")
    public ResponseEntity<List<StudentDTO>> getStudentsForSubject(@PathVariable Long id) {
        List<LessonPlan> lessonPlans = lessonPlanRepository.findBySubjectId(id);
        List<Student> students = lessonPlans.stream()
                .flatMap(lessonPlan -> lessonPlan.getStudentGroup().getStudents().stream())
                .distinct()
                .collect(Collectors.toList());

        List<StudentDTO> studentDTOs = students.stream()
                .map(student -> {
                    UserRepresentation user = keycloakService.getUsersDetails(Collections.singletonList(student.getKeycloakId())).get(0);
                    return new StudentDTO(student, user);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(studentDTOs);
    }
}

