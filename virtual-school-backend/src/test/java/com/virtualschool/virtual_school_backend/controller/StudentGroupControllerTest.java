package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Student;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.repository.StudentGroupRepository;
import com.virtualschool.virtual_school_backend.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

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

    @Test
    void getAllGroups() throws Exception {
        StudentGroup group1 = new StudentGroup("Class A");
        StudentGroup group2 = new StudentGroup("Class B");
        when(studentGroupRepository.findAllWithStudents()).thenReturn(Arrays.asList(group1, group2));

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Class A")));
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
        Student student = new Student("John", "Doe", "john.doe@example.com");
        student.setId(1L);
        StudentGroup group = new StudentGroup("Class A");
        group.setId(2L);
        group.setStudents(new HashSet<>(Arrays.asList(student)));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(studentGroupRepository.save(any(StudentGroup.class))).thenReturn(group);

        mockMvc.perform(post("/api/groups/2/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students", hasSize(1)))
                .andExpect(jsonPath("$.students[0].firstName", is("John")));
    }
}
