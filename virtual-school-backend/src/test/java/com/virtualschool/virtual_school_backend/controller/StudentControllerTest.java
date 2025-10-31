package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Student;
import com.virtualschool.virtual_school_backend.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void getAllStudents() throws Exception {
        Student student1 = new Student("John", "Doe", "john.doe@example.com");
        Student student2 = new Student("Jane", "Doe", "jane.doe@example.com");
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    void getStudentById() throws Exception {
        Student student = new Student("John", "Doe", "john.doe@example.com");
        student.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    void createStudent() throws Exception {
        Student student = new Student("John", "Doe", "john.doe@example.com");
        student.setId(1L);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}
