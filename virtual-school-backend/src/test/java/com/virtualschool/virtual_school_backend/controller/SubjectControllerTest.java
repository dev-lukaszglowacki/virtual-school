package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
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

@WebMvcTest(controllers = SubjectController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@ActiveProfiles("test")
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubjectRepository subjectRepository;

    @Test
    void getAllSubjects() throws Exception {
        Subject subject1 = new Subject("Mathematics", "Advanced Calculus");
        Subject subject2 = new Subject("History", "World History");
        when(subjectRepository.findAll()).thenReturn(Arrays.asList(subject1, subject2));

        mockMvc.perform(get("/api/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Mathematics")));
    }

    @Test
    void getSubjectById() throws Exception {
        Subject subject = new Subject("Mathematics", "Advanced Calculus");
        subject.setId(1L);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        mockMvc.perform(get("/api/subjects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Mathematics")));
    }

    @Test
    void createSubject() throws Exception {
        Subject subject = new Subject("Mathematics", "Advanced Calculus");
        subject.setId(1L);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        mockMvc.perform(post("/api/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Mathematics\",\"description\":\"Advanced Calculus\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}
