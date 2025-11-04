package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.dto.LecturerDTO;
import com.virtualschool.virtual_school_backend.model.Lecturer;
import com.virtualschool.virtual_school_backend.repository.LecturerRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    private final LecturerRepository lecturerRepository;
    private final KeycloakService keycloakService;

    public LecturerController(LecturerRepository lecturerRepository, KeycloakService keycloakService) {
        this.lecturerRepository = lecturerRepository;
        this.keycloakService = keycloakService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'teacher')")
    @Transactional(readOnly = true)
    public List<LecturerDTO> getAllLecturers() {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<String> keycloakIds = lecturers.stream().map(Lecturer::getKeycloakId).collect(Collectors.toList());

        Map<String, UserRepresentation> userMap = keycloakService.getUsersDetails(keycloakIds).stream()
                .collect(Collectors.toMap(UserRepresentation::getId, Function.identity()));

        return lecturers.stream()
                .map(lecturer -> new LecturerDTO(lecturer, userMap.get(lecturer.getKeycloakId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('teacher')")
    @Transactional(readOnly = true)
    public ResponseEntity<LecturerDTO> getMe(@AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return lecturerRepository.findByKeycloakId(keycloakId)
                .map(lecturer -> {
                    UserRepresentation user = keycloakService.getUsersDetails(List.of(keycloakId)).get(0);
                    return ResponseEntity.ok(new LecturerDTO(lecturer, user));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
