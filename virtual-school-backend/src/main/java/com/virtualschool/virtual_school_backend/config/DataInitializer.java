package com.virtualschool.virtual_school_backend.config;

import com.virtualschool.virtual_school_backend.model.*;
import com.virtualschool.virtual_school_backend.repository.*;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final KeycloakService keycloakService;
    private final LecturerRepository lecturerRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final LessonPlanRepository lessonPlanRepository;

    public DataInitializer(KeycloakService keycloakService,
                           LecturerRepository lecturerRepository,
                           SubjectRepository subjectRepository,
                           StudentRepository studentRepository,
                           StudentGroupRepository studentGroupRepository,
                           LessonPlanRepository lessonPlanRepository) {
        this.keycloakService = keycloakService;
        this.lecturerRepository = lecturerRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
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
                UserRepresentation teacherUser = keycloakService.findByUsername("teacheruser");
                Lecturer lecturer = null;
                if (teacherUser != null) {
                    if (lecturerRepository.findByKeycloakId(teacherUser.getId()).isEmpty()) {
                        lecturer = new Lecturer(teacherUser.getId());
                        lecturerRepository.save(lecturer);

                        Subject history = subjectRepository.findByName("History");
                        if (history != null) {
                            history.setLecturer(lecturer);
                            subjectRepository.save(history);
                        }

                        Subject biology = subjectRepository.findByName("Biology");
                        if (biology != null) {
                            biology.setLecturer(lecturer);
                            subjectRepository.save(biology);
                        }
                    } else {
                        lecturer = lecturerRepository.findByKeycloakId(teacherUser.getId()).get();
                    }
                }

                UserRepresentation studentUser = keycloakService.findByUsername("studentuser");
                if (studentUser != null) {
                    if (studentRepository.findByKeycloakId(studentUser.getId()).isEmpty()) {
                        Student student = new Student(studentUser.getId());
                        studentRepository.save(student);

                        StudentGroup groupA1 = studentGroupRepository.findByName("A1");
                        if (groupA1 == null) {
                            groupA1 = new StudentGroup("A1");
                        }

                        groupA1.getStudents().add(student);
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
                        lesson1.setLecturer(lecturer);
                        lesson1.setDayOfWeek(DayOfWeek.MONDAY);
                        lesson1.setStartTime(LocalTime.of(9, 0));
                        lesson1.setEndTime(LocalTime.of(10, 0));
                        lessonPlanRepository.save(lesson1);

                        LessonPlan lesson2 = new LessonPlan();
                        lesson2.setStudentGroup(groupA1);
                        lesson2.setSubject(biology);
                        lesson2.setLecturer(lecturer);
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
