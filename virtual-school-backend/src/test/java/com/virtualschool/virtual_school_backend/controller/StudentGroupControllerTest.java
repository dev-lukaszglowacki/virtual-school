package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Role;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.StudentGroupRepository;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

@WebMvcTest(excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@ActiveProfiles("test")
class StudentGroupControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentGroupRepository studentGroupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private StudentGroupController studentGroupController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentGroupController).build();
    }

    @Test
    void getAllGroups() throws Exception {
        User student1 = new User("keycloak-id-1", Role.STUDENT);
        student1.setId(1L);
        User student2 = new User("keycloak-id-2", Role.STUDENT);
        student2.setId(2L);

        StudentGroup group1 = new StudentGroup("Class A");
        group1.setId(10L);
        group1.setUsers(new HashSet<>(Arrays.asList(student1)));

        StudentGroup group2 = new StudentGroup("Class B");
        group2.setId(20L);
        group2.setUsers(new HashSet<>(Arrays.asList(student2)));

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
        User student = new User("keycloak-id-1", Role.STUDENT);
        student.setId(1L);
        StudentGroup group = new StudentGroup("Class A");
        group.setId(2L);
        group.setUsers(new HashSet<>(Arrays.asList(student)));

        UserRepresentation user = new UserRepresentation();
        user.setId("keycloak-id-1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
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
        User student = new User("keycloak-id-1", Role.STUDENT);
        student.setId(1L);
        StudentGroup group = new StudentGroup("Class A");
        group.setId(2L);
        group.setUsers(new HashSet<>(Arrays.asList(student)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(studentGroupRepository.save(any(StudentGroup.class))).thenReturn(group);

        mockMvc.perform(delete("/api/groups/2/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(0)));
    }
}
