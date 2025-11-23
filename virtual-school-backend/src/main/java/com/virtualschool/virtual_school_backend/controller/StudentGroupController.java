package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.StudentGroupDTO;
import com.virtualschool.virtual_school_backend.dto.UserDTO;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.StudentGroupRepository;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
public class StudentGroupController {

    private static final Logger logger = LoggerFactory.getLogger(StudentGroupController.class);

    private final StudentGroupRepository studentGroupRepository;
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    public StudentGroupController(StudentGroupRepository studentGroupRepository, UserRepository userRepository, KeycloakService keycloakService) {
        this.studentGroupRepository = studentGroupRepository;
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher', 'student')")
    public List<StudentGroupDTO> getAllGroups() {
        List<StudentGroup> groupsWithStudents = studentGroupRepository.findAllWithStudents();
        logger.info("Groups fetched from repository: {}", groupsWithStudents);

        // Get all unique student keycloakIds from all groups
        List<String> allKeycloakIds = groupsWithStudents.stream()
                .flatMap(group -> group.getUsers().stream())
                .map(User::getKeycloakId)
                .distinct()
                .collect(Collectors.toList());

        // Fetch all user details from keycloak in one go
        Map<String, UserRepresentation> userMap = keycloakService.getUsersDetails(allKeycloakIds).stream()
                .collect(Collectors.toMap(UserRepresentation::getId, Function.identity()));

        // Map groups to DTOs, enriching student data
        List<StudentGroupDTO> groups = groupsWithStudents.stream()
                .map(group -> {
                    Set<UserDTO> studentDTOs = group.getUsers().stream()
                            .map(user -> new UserDTO(user, userMap.get(user.getKeycloakId())))
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
        User student = userRepository.findById(studentId).orElse(null);
        StudentGroup group = studentGroupRepository.findById(groupId).orElse(null);

        if (student == null || group == null) {
            return ResponseEntity.notFound().build();
        }

        group.getUsers().add(student);
        studentGroupRepository.save(group);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{groupId}/students/{studentId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StudentGroup> removeStudentFromGroup(@PathVariable Long groupId, @PathVariable Long studentId) {
        User student = userRepository.findById(studentId).orElse(null);
        StudentGroup group = studentGroupRepository.findById(groupId).orElse(null);

        if (student == null || group == null) {
            return ResponseEntity.notFound().build();
        }

        group.getUsers().remove(student);
        studentGroupRepository.save(group);
        return ResponseEntity.ok(group);
    }
}
