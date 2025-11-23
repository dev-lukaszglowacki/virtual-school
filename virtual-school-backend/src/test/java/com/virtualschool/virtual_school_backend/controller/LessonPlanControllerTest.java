package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.model.Role;
import com.virtualschool.virtual_school_backend.repository.LessonPlanRepository;
import com.virtualschool.virtual_school_backend.repository.StudentGroupRepository;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@ActiveProfiles("test")
class LessonPlanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LessonPlanRepository lessonPlanRepository;
    @Mock
    private KeycloakService keycloakService;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentGroupRepository studentGroupRepository;

    @InjectMocks
    private LessonPlanController lessonPlanController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(lessonPlanController).build();
    }

    @Test
    void getAllLessonPlans() throws Exception {
        User lecturer = new User("test-lecturer-keycloak-id", Role.LECTURER);
        lecturer.setId(1L);
        Subject subject = new Subject("Math", "Mathematics");
        subject.setId(1L);
        StudentGroup group = new StudentGroup("Class A");
        group.setId(1L);
        LessonPlan plan1 = new LessonPlan();
        plan1.setId(1L);
        plan1.setSubject(subject);
        plan1.setUser(lecturer);
        plan1.setStudentGroup(group);
        plan1.setDayOfWeek(DayOfWeek.MONDAY);
        plan1.setStartTime(LocalTime.of(9, 0));
        plan1.setEndTime(LocalTime.of(10, 0));

        when(lessonPlanRepository.findAll()).thenReturn(Arrays.asList(plan1));
        when(keycloakService.getUsersDetails(any())).thenReturn(Arrays.asList(new org.keycloak.representations.idm.UserRepresentation() {{
            setFirstName("Test");
            setLastName("Lecturer");
        }}));

        mockMvc.perform(get("/api/lesson-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].subjectName", is("Math")))
                .andExpect(jsonPath("$[0].lecturerName", is("Test Lecturer")));
    }

    @Test
    void createLessonPlan() throws Exception {
        User lecturer = new User("test-lecturer-keycloak-id", Role.LECTURER);
        lecturer.setId(2L);
        Subject subject = new Subject("Math", "Mathematics");
        subject.setId(1L);
        StudentGroup group = new StudentGroup("Class A");
        group.setId(3L);

        LessonPlan plan = new LessonPlan();
        plan.setId(1L);
        plan.setSubject(subject);
        plan.setUser(lecturer);
        plan.setStudentGroup(group);
        plan.setDayOfWeek(DayOfWeek.MONDAY);
        plan.setStartTime(LocalTime.of(9, 0));
        plan.setEndTime(LocalTime.of(10, 0));

        when(subjectRepository.findById(any())).thenReturn(java.util.Optional.of(subject));
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(lecturer));
        when(studentGroupRepository.findById(any())).thenReturn(java.util.Optional.of(group));
        when(lessonPlanRepository.save(any(LessonPlan.class))).thenReturn(plan);

        mockMvc.perform(post("/api/lesson-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"subjectId\":1,\"lecturerId\":2,\"groupId\":3,\"dayOfWeek\":\"MONDAY\",\"startTime\":\"09:00\",\"endTime\":\"10:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}
