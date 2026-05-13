package com.virtualschool.virtual_school_backend.service;

import com.virtualschool.virtual_school_backend.dto.UserDTO;
import com.virtualschool.virtual_school_backend.model.Role;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    public UserService(UserRepository userRepository, KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.keycloakService = keycloakService;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return getUsers(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllStudents() {
        return getUsers(userRepository.findByRole(Role.STUDENT));
    }

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

    @Transactional(readOnly = true)
    public ResponseEntity<UserDTO> getMe(String keycloakId) { // Modified to accept keycloakId
        return userRepository.findByKeycloakId(keycloakId)
                .map(user -> {
                    UserRepresentation userRepresentation = keycloakService.getUsersDetails(List.of(keycloakId)).get(0);
                    return ResponseEntity.ok(new UserDTO(user, userRepresentation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Set<StudentGroup>> getMyGroups(String keycloakId) { // Modified to accept keycloakId
        return userRepository.findByKeycloakId(keycloakId)
                .map(user -> ResponseEntity.ok(user.getGroups()))
                .orElse(ResponseEntity.notFound().build());
    }
}
