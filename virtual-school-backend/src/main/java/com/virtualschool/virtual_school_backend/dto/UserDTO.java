package com.virtualschool.virtual_school_backend.dto;

import com.virtualschool.virtual_school_backend.model.User;
import org.keycloak.representations.idm.UserRepresentation;

public class UserDTO {
    private Long id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String role;

    public UserDTO() {
    }

    public UserDTO(User user, UserRepresentation userRepresentation) {
        this.id = user.getId();
        this.keycloakId = user.getKeycloakId();
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
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}