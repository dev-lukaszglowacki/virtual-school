package com.virtualschool.virtual_school_backend.config;

import com.virtualschool.virtual_school_backend.model.Role;
import com.virtualschool.virtual_school_backend.model.User;
import com.virtualschool.virtual_school_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalTime;
import com.virtualschool.virtual_school_backend.model.LessonPlan;
import com.virtualschool.virtual_school_backend.model.StudentGroup;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.repository.LessonPlanRepository;
import com.virtualschool.virtual_school_backend.repository.StudentGroupRepository;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;

@Component
public class DataInitializer implements CommandLineRunner {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final LessonPlanRepository lessonPlanRepository;

    public DataInitializer(KeycloakService keycloakService,
                           UserRepository userRepository,
                           SubjectRepository subjectRepository,
                           StudentGroupRepository studentGroupRepository,
                           LessonPlanRepository lessonPlanRepository) {
        this.keycloakService = keycloakService;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.lessonPlanRepository = lessonPlanRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        int maxRetries = 10;
        long delay = 5000; // 5 seconds

        for (int i = 0; i < maxRetries; i++) {
            try {
                UserRepresentation teacherUserRep = keycloakService.findByUsername("teacheruser");
                User lecturer = null;
                if (teacherUserRep != null) {
                    if (userRepository.findByKeycloakId(teacherUserRep.getId()).isEmpty()) {
                        lecturer = new User(teacherUserRep.getId(), Role.LECTURER);
                        userRepository.save(lecturer);
                    } else {
                        lecturer = userRepository.findByKeycloakId(teacherUserRep.getId()).get();
                    }
                }

                UserRepresentation studentUserRep = keycloakService.findByUsername("studentuser");
                if (studentUserRep != null) {
                    if (userRepository.findByKeycloakId(studentUserRep.getId()).isEmpty()) {
                        User student = new User(studentUserRep.getId(), Role.STUDENT);
                        userRepository.save(student);

                        StudentGroup groupA1 = studentGroupRepository.findByName("A1");
                        if (groupA1 == null) {
                            groupA1 = new StudentGroup("A1");
                        }

                        groupA1.getUsers().add(student);
                        studentGroupRepository.save(groupA1);
                    }
                }

                StudentGroup groupA1 = studentGroupRepository.findByName("A1");
                if (groupA1 != null && lessonPlanRepository.findByStudentGroupId(groupA1.getId()).isEmpty()) {
                    Subject history = subjectRepository.findByName("History");
                    Subject biology = subjectRepository.findByName("Biology");

                    if (history != null && biology != null && lecturer != null) {
                        LessonPlan lesson1 = new LessonPlan();
                        lesson1.setStudentGroup(groupA1);
                        lesson1.setSubject(history);
                        lesson1.setUser(lecturer);
                        lesson1.setDayOfWeek(DayOfWeek.MONDAY);
                        lesson1.setStartTime(LocalTime.of(9, 0));
                        lesson1.setEndTime(LocalTime.of(10, 0));
                        lessonPlanRepository.save(lesson1);

                        LessonPlan lesson2 = new LessonPlan();
                        lesson2.setStudentGroup(groupA1);
                        lesson2.setSubject(biology);
                        lesson2.setUser(lecturer);
                        lesson2.setDayOfWeek(DayOfWeek.TUESDAY);
                        lesson2.setStartTime(LocalTime.of(10, 0));
                        lesson2.setEndTime(LocalTime.of(11, 0));
                        lessonPlanRepository.save(lesson2);
                    }
                }

                return;
            } catch (Exception e) {
                System.err.println("Failed to connect to Keycloak, retrying in " + delay + "ms... (" + (i + 1) + "/" + maxRetries + ")");
                Thread.sleep(delay);
            }
        }
        throw new RuntimeException("Failed to connect to Keycloak after " + maxRetries + " retries.");
    }
}
