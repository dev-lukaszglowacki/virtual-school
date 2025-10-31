package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Student;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.repository.StudentGroupRepository;
import com.virtualschool.virtual_school_backend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.virtualschool.virtual_school_backend.dto.StudentDTO;
import com.virtualschool.virtual_school_backend.dto.StudentGroupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class StudentGroupController {

    private static final Logger logger = LoggerFactory.getLogger(StudentGroupController.class);

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public List<StudentGroupDTO> getAllGroups() {
        List<StudentGroup> groupsWithStudents = studentGroupRepository.findAllWithStudents();
        logger.info("Groups fetched from repository: {}", groupsWithStudents);

        List<StudentGroupDTO> groups = groupsWithStudents.stream()
                .map(group -> {
                    Set<StudentDTO> studentDTOs = group.getStudents().stream()
                            .map(student -> new StudentDTO(student.getId(), student.getFirstName(), student.getLastName(), student.getEmail()))
                            .collect(Collectors.toSet());
                    return new StudentGroupDTO(group.getId(), group.getName(), studentDTOs);
                })
                .collect(Collectors.toList());
        logger.info("Groups being returned: {}", groups);
        return groups;
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public StudentGroup createGroup(@RequestBody StudentGroup studentGroup) {
        return studentGroupRepository.save(studentGroup);
    }

    @PutMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StudentGroup> updateGroup(@PathVariable Long groupId, @RequestBody StudentGroup groupDetails) {
        return studentGroupRepository.findById(groupId)
                .map(group -> {
                    group.setName(groupDetails.getName());
                    return ResponseEntity.ok(studentGroupRepository.save(group));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        return studentGroupRepository.findById(groupId)
                .map(group -> {
                    studentGroupRepository.delete(group);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{groupId}/students/{studentId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StudentGroup> addStudentToGroup(@PathVariable Long groupId, @PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        StudentGroup group = studentGroupRepository.findById(groupId).orElse(null);

        if (student == null || group == null) {
            return ResponseEntity.notFound().build();
        }

        group.getStudents().add(student);
        studentGroupRepository.save(group);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{groupId}/students/{studentId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StudentGroup> removeStudentFromGroup(@PathVariable Long groupId, @PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        StudentGroup group = studentGroupRepository.findById(groupId).orElse(null);

        if (student == null || group == null) {
            return ResponseEntity.notFound().build();
        }

        group.getStudents().remove(student);
        studentGroupRepository.save(group);
        return ResponseEntity.ok(group);
    }
}
