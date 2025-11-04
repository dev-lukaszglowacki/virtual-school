package com.virtualschool.virtual_school_backend.dto;

import com.virtualschool.virtual_school_backend.model.Lecturer;
import org.keycloak.representations.idm.UserRepresentation;

public class LecturerDTO {
    private Long id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;

    public LecturerDTO() {
    }

    public LecturerDTO(Lecturer lecturer, UserRepresentation userRepresentation) {
        this.id = lecturer.getId();
        this.keycloakId = lecturer.getKeycloakId();
        this.firstName = userRepresentation.getFirstName();
        this.lastName = userRepresentation.getLastName();
        this.email = userRepresentation.getEmail();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKeycloakId() { return keycloakId; }
    public void setKeycloakId(String keycloakId) { this.keycloakId = keycloakId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
