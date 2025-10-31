package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Lecturer;
import com.virtualschool.virtual_school_backend.repository.LecturerRepository;
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

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;

@WebMvcTest(controllers = LecturerController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@ActiveProfiles("test")
class LecturerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LecturerRepository lecturerRepository;

    @Test
    void getAllLecturers() throws Exception {
        Lecturer lecturer1 = new Lecturer("John", "Smith", "john.smith@example.com", "Math");
        Lecturer lecturer2 = new Lecturer("Jane", "Smith", "jane.smith@example.com", "History");
        when(lecturerRepository.findAll()).thenReturn(Arrays.asList(lecturer1, lecturer2));

        mockMvc.perform(get("/api/lecturers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    void getLecturerById() throws Exception {
        Lecturer lecturer = new Lecturer("John", "Smith", "john.smith@example.com", "Math");
        lecturer.setId(1L);
        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));

        mockMvc.perform(get("/api/lecturers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    void createLecturer() throws Exception {
        Lecturer lecturer = new Lecturer("John", "Smith", "john.smith@example.com", "Math");
        lecturer.setId(1L);
        when(lecturerRepository.save(any(Lecturer.class))).thenReturn(lecturer);

        mockMvc.perform(post("/api/lecturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"John\",\"lastName\":\"Smith\",\"email\":\"john.smith@example.com\",\"subject\":\"Math\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}
