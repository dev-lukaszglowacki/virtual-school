package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.UserDTO;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('admin', 'teacher')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('admin', 'teacher')")
    public List<UserDTO> getAllStudents() {
        return userService.getAllStudents();
    }

    @GetMapping("/lecturers")
    @PreAuthorize("hasAnyRole('admin', 'teacher')")
    public List<UserDTO> getAllLecturers() {
        return userService.getAllLecturers();
    }

    @GetMapping("/users/me")
    @PreAuthorize("hasAnyRole('student', 'teacher')")
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return userService.getMe(keycloakId);
    }

    @GetMapping("/users/me/groups")
    @PreAuthorize("hasRole('student')")
    public ResponseEntity<Set<StudentGroup>> getMyGroups(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return userService.getMyGroups(keycloakId);
    }
}
