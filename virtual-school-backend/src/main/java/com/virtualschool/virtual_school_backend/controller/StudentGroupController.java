package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.StudentGroupDTO;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.service.StudentGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/groups")
public class StudentGroupController {

    private final StudentGroupService studentGroupService;

    public StudentGroupController(StudentGroupService studentGroupService) {
        this.studentGroupService = studentGroupService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public List<StudentGroupDTO> getAllGroups() {
        return studentGroupService.getAllGroups();
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StudentGroup> createGroup(@NonNull @RequestBody StudentGroup studentGroup) {
        return studentGroupService.createGroup(studentGroup);
    }

    @PutMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StudentGroup> updateGroup(@NonNull @PathVariable Long groupId, @RequestBody StudentGroup groupDetails) {
        return studentGroupService.updateGroup(groupId, groupDetails);
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteGroup(@NonNull @PathVariable Long groupId) {
        return studentGroupService.deleteGroup(groupId);
    }

    @PostMapping("/{groupId}/students/{studentId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StudentGroup> addStudentToGroup(@NonNull @PathVariable Long groupId, @NonNull @PathVariable Long studentId) {
        return studentGroupService.addStudentToGroup(groupId, studentId);
    }

    @DeleteMapping("/{groupId}/students/{studentId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StudentGroup> removeStudentFromGroup(@NonNull @PathVariable Long groupId, @NonNull @PathVariable Long studentId) {
        return studentGroupService.removeStudentFromGroup(groupId, studentId);
    }
}
