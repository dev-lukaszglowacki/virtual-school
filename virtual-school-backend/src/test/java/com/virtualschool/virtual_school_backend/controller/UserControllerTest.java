package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Role;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@ActiveProfiles("test")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllStudents() throws Exception {
        User student1 = new User("keycloak-id-1", Role.STUDENT);
        student1.setId(1L);
        User student2 = new User("keycloak-id-2", Role.STUDENT);
        student2.setId(2L);

        UserRepresentation user1 = new UserRepresentation();
        user1.setId("keycloak-id-1");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");

        UserRepresentation user2 = new UserRepresentation();
        user2.setId("keycloak-id-2");
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setEmail("jane.doe@example.com");

        when(userRepository.findByRole(Role.STUDENT)).thenReturn(Arrays.asList(student1, student2));
        when(keycloakService.getUsersDetails(anyList())).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));
    }

    @Test
    void getAllLecturers() throws Exception {
        User lecturer1 = new User("keycloak-id-1", Role.LECTURER);
        lecturer1.setId(1L);
        User lecturer2 = new User("keycloak-id-2", Role.LECTURER);
        lecturer2.setId(2L);

        UserRepresentation user1 = new UserRepresentation();
        user1.setId("keycloak-id-1");
        user1.setFirstName("John");
        user1.setLastName("Smith");
        user1.setEmail("john.smith@example.com");

        UserRepresentation user2 = new UserRepresentation();
        user2.setId("keycloak-id-2");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");

        when(userRepository.findByRole(Role.LECTURER)).thenReturn(Arrays.asList(lecturer1, lecturer2));
        when(keycloakService.getUsersDetails(anyList())).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/lecturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));
    }
}
