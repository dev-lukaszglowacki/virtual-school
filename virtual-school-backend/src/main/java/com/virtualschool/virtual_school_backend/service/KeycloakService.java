package com.virtualschool.virtual_school_backend.service;

import com.virtualschool.virtual_school_backend.model.Role;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.UserRepository;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;

    public KeycloakService(Keycloak keycloak, UserRepository userRepository) {
        this.keycloak = keycloak;
        this.userRepository = userRepository;
    }

    public void createUser(String username, String password, String firstName, String lastName, String email, String roleName) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm("virtual-school").users().create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in Keycloak. Response: " + response.readEntity(String.class));
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        RoleRepresentation role = keycloak.realm("virtual-school").roles().get(roleName).toRepresentation();
        UserResource userResource = keycloak.realm("virtual-school").users().get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(role));

        if ("student".equalsIgnoreCase(roleName)) {
            User student = new User(userId, Role.STUDENT);
            userRepository.save(student);
        } else if ("teacher".equalsIgnoreCase(roleName)) {
            User lecturer = new User(userId, Role.LECTURER);
            userRepository.save(lecturer);
        }
    }

    public List<UserRepresentation> getUsersDetails(List<String> keycloakIds) {
        return keycloakIds.stream()
                .map(id -> keycloak.realm("virtual-school").users().get(id).toRepresentation())
                .collect(Collectors.toList());
    }

    public UserRepresentation findByUsername(String username) {
        List<UserRepresentation> users = keycloak.realm("virtual-school").users().search(username, true);
        return users.stream().findFirst().orElse(null);
    }
}
