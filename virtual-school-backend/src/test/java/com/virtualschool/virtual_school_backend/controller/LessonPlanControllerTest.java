package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.model.Lecturer;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.repository.LessonPlanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;

@WebMvcTest(controllers = LessonPlanController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@ActiveProfiles("test")
class LessonPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LessonPlanRepository lessonPlanRepository;

    @Test
    void getAllLessonPlans() throws Exception {
        Lecturer lecturer = new Lecturer("test-lecturer-keycloak-id");
        Subject subject = new Subject("Math", "Mathematics", lecturer);
        StudentGroup group = new StudentGroup("Class A");
        LessonPlan plan1 = new LessonPlan();
        plan1.setId(1L);
        plan1.setSubject(subject);
        plan1.setLecturer(lecturer);
        plan1.setStudentGroup(group);
        plan1.setDayOfWeek(DayOfWeek.MONDAY);
        plan1.setStartTime(LocalTime.of(9, 0));
        plan1.setEndTime(LocalTime.of(10, 0));

        when(lessonPlanRepository.findAll()).thenReturn(Arrays.asList(plan1));

        mockMvc.perform(get("/api/lesson-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].subject.name", is("Math")));
    }

    @Test
    void createLessonPlan() throws Exception {
        Lecturer lecturer = new Lecturer("test-lecturer-keycloak-id");
        Subject subject = new Subject("Math", "Mathematics", lecturer);
        lecturer.setId(2L);
        StudentGroup group = new StudentGroup("Class A");
        group.setId(3L);

        LessonPlan plan = new LessonPlan();
        plan.setId(1L);
        plan.setSubject(subject);
        plan.setLecturer(lecturer);
        plan.setStudentGroup(group);
        plan.setDayOfWeek(DayOfWeek.MONDAY);
        plan.setStartTime(LocalTime.of(9, 0));
        plan.setEndTime(LocalTime.of(10, 0));

        when(lessonPlanRepository.save(any(LessonPlan.class))).thenReturn(plan);

        mockMvc.perform(post("/api/lesson-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"subject\":{\"id\":1},\"lecturer\":{\"id\":2},\"studentGroup\":{\"id\":3},\"dayOfWeek\":\"MONDAY\",\"startTime\":\"09:00:00\",\"endTime\":\"10:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}
