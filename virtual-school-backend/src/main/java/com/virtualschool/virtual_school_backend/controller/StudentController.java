package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.StudentDTO;
import com.virtualschool.virtual_school_backend.model.Student;
import com.virtualschool.virtual_school_backend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(StudentDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('student')")
    @Transactional(readOnly = true)
    public ResponseEntity<StudentDTO> getMe(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        return studentRepository.findByEmail(email)
                .map(student -> ResponseEntity.ok(new StudentDTO(student)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    @Transactional(readOnly = true)
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(student -> ResponseEntity.ok(new StudentDTO(student)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.setFirstName(studentDetails.getFirstName());
                    student.setLastName(studentDetails.getLastName());
                    student.setEmail(studentDetails.getEmail());
                    Student updatedStudent = studentRepository.save(student);
                    return ResponseEntity.ok(new StudentDTO(updatedStudent));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(student -> {
                    studentRepository.delete(student);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
