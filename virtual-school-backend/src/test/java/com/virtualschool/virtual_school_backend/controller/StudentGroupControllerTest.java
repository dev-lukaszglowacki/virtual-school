package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Student;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.repository.StudentGroupRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;

@WebMvcTest(controllers = StudentGroupController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@ActiveProfiles("test")
class StudentGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentGroupRepository studentGroupRepository;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private KeycloakService keycloakService;

    @Test
    void getAllGroups() throws Exception {
        Student student1 = new Student("keycloak-id-1");
        student1.setId(1L);
        Student student2 = new Student("keycloak-id-2");
        student2.setId(2L);

        StudentGroup group1 = new StudentGroup("Class A");
        group1.setId(10L);
        group1.setStudents(new HashSet<>(Arrays.asList(student1)));

        StudentGroup group2 = new StudentGroup("Class B");
        group2.setId(20L);
        group2.setStudents(new HashSet<>(Arrays.asList(student2)));

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

        when(studentGroupRepository.findAllWithStudents()).thenReturn(Arrays.asList(group1, group2));
        when(keycloakService.getUsersDetails(anyList())).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Class A")))
                .andExpect(jsonPath("$[0].students", hasSize(1)))
                .andExpect(jsonPath("$[0].students[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].name", is("Class B")))
                .andExpect(jsonPath("$[1].students", hasSize(1)))
                .andExpect(jsonPath("$[1].students[0].firstName", is("Jane")));
    }

    @Test
    void createGroup() throws Exception {
        StudentGroup group = new StudentGroup("Class C");
        group.setId(1L);
        when(studentGroupRepository.save(any(StudentGroup.class))).thenReturn(group);

        mockMvc.perform(post("/api/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Class C\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void addStudentToGroup() throws Exception {
        Student student = new Student("keycloak-id-1");
        student.setId(1L);
        StudentGroup group = new StudentGroup("Class A");
        group.setId(2L);
        group.setStudents(new HashSet<>(Arrays.asList(student)));

        UserRepresentation user = new UserRepresentation();
        user.setId("keycloak-id-1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(studentGroupRepository.save(any(StudentGroup.class))).thenReturn(group);
        when(keycloakService.getUsersDetails(anyList())).thenReturn(List.of(user));

        mockMvc.perform(post("/api/groups/2/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(1)))
                .andExpect(jsonPath("$.students[0].keycloakId", is("keycloak-id-1")));
    }

    @Test
    void removeStudentFromGroup() throws Exception {
        Student student = new Student("keycloak-id-1");
        student.setId(1L);
        StudentGroup group = new StudentGroup("Class A");
        group.setId(2L);
        group.setStudents(new HashSet<>(Arrays.asList(student)));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(studentGroupRepository.save(any(StudentGroup.class))).thenReturn(group);

        mockMvc.perform(delete("/api/groups/2/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(0)));
    }
}
