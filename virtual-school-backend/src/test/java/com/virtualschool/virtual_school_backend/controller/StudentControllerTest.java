package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Student;
import com.virtualschool.virtual_school_backend.repository.StudentRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;

@WebMvcTest(controllers = StudentController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@ActiveProfiles("test")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private KeycloakService keycloakService;

    @Test
    void getAllStudents() throws Exception {
        Student student1 = new Student("keycloak-id-1");
        student1.setId(1L);
        Student student2 = new Student("keycloak-id-2");
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

        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));
        when(keycloakService.getUsersDetails(anyList())).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));
    }
}
