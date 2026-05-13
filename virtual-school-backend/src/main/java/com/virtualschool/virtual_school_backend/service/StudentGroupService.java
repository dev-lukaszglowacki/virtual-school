package com.virtualschool.virtual_school_backend.service;

import com.virtualschool.virtual_school_backend.dto.StudentGroupDTO;
import com.virtualschool.virtual_school_backend.dto.UserDTO;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.StudentGroupRepository;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StudentGroupService {

    private static final Logger logger = LoggerFactory.getLogger(StudentGroupService.class);

    private final StudentGroupRepository studentGroupRepository;
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    public StudentGroupService(StudentGroupRepository studentGroupRepository, UserRepository userRepository, KeycloakService keycloakService) {
        this.studentGroupRepository = studentGroupRepository;
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
    }

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

    public ResponseEntity<StudentGroup> createGroup(@NonNull StudentGroup studentGroup) {
        return ResponseEntity.ok(studentGroupRepository.save(studentGroup));
    }

    public ResponseEntity<StudentGroup> updateGroup(@NonNull Long groupId, @NonNull StudentGroup groupDetails) {
        return studentGroupRepository.findById(groupId)
                .map(group -> {
                    group.setName(groupDetails.getName());
                    return ResponseEntity.ok(studentGroupRepository.save(group));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> deleteGroup(@NonNull Long groupId) {
        return studentGroupRepository.findById(groupId)
                .map(group -> {
                    Objects.requireNonNull(group, "StudentGroup cannot be null");
                    studentGroupRepository.delete(group);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<StudentGroup> addStudentToGroup(@NonNull Long groupId, @NonNull Long studentId) {
        User student = userRepository.findById(studentId).orElse(null);
        StudentGroup group = studentGroupRepository.findById(groupId).orElse(null);

        if (student == null || group == null) {
            return ResponseEntity.notFound().build();
        }

        group.getUsers().add(student);
        return ResponseEntity.ok(studentGroupRepository.save(group));
    }

    public ResponseEntity<StudentGroup> removeStudentFromGroup(@NonNull Long groupId, @NonNull Long studentId) {
        User student = userRepository.findById(studentId).orElse(null);
        StudentGroup group = studentGroupRepository.findById(groupId).orElse(null);

        if (student == null || group == null) {
            return ResponseEntity.notFound().build();
        }

        group.getUsers().remove(student);
        return ResponseEntity.ok(studentGroupRepository.save(group));
    }
}
