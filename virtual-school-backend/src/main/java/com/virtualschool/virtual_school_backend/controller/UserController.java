package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.UserDTO;
import com.virtualschool.virtual_school_backend.model.Role;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    public UserController(UserRepository userRepository, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('admin', 'teacher')")
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return getUsers(userRepository.findAll());
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('admin', 'teacher')")
    @Transactional(readOnly = true)
    public List<UserDTO> getAllStudents() {
        return getUsers(userRepository.findByRole(Role.STUDENT));
    }

    @GetMapping("/lecturers")
    @PreAuthorize("hasAnyRole('admin', 'teacher')")
    @Transactional(readOnly = true)
    public List<UserDTO> getAllLecturers() {
        return getUsers(userRepository.findByRole(Role.LECTURER));
    }

    private List<UserDTO> getUsers(List<User> users) {
        List<String> keycloakIds = users.stream().map(User::getKeycloakId).collect(Collectors.toList());

        Map<String, UserRepresentation> userMap = keycloakService.getUsersDetails(keycloakIds).stream()
                .collect(Collectors.toMap(UserRepresentation::getId, Function.identity()));

        return users.stream()
                .map(user -> new UserDTO(user, userMap.get(user.getKeycloakId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/users/me")
    @PreAuthorize("hasAnyRole('student', 'teacher')")
    @Transactional(readOnly = true)
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return userRepository.findByKeycloakId(keycloakId)
                .map(user -> {
                    UserRepresentation userRepresentation = keycloakService.getUsersDetails(List.of(keycloakId)).get(0);
                    return ResponseEntity.ok(new UserDTO(user, userRepresentation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/me/groups")
    @PreAuthorize("hasRole('student')")
    @Transactional(readOnly = true)
    public ResponseEntity<Set<StudentGroup>> getMyGroups(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return userRepository.findByKeycloakId(keycloakId)
                .map(user -> ResponseEntity.ok(user.getGroups()))
                .orElse(ResponseEntity.notFound().build());
    }
}
