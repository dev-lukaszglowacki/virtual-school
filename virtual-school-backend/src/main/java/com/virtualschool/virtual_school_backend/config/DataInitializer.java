package com.virtualschool.virtual_school_backend.config;

import com.virtualschool.virtual_school_backend.model.Lecturer;
import com.virtualschool.virtual_school_backend.model.Student;
import com.virtualschool.virtual_school_backend.model.Subject;
import com.virtualschool.virtual_school_backend.repository.LecturerRepository;
import com.virtualschool.virtual_school_backend.repository.StudentRepository;
import com.virtualschool.virtual_school_backend.repository.SubjectRepository;
import com.virtualschool.virtual_school_backend.service.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final KeycloakService keycloakService;
    private final LecturerRepository lecturerRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    public DataInitializer(KeycloakService keycloakService,
                           LecturerRepository lecturerRepository,
                           SubjectRepository subjectRepository,
                           StudentRepository studentRepository) {
        this.keycloakService = keycloakService;
        this.lecturerRepository = lecturerRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        int maxRetries = 10;
        long delay = 5000; // 5 seconds

        for (int i = 0; i < maxRetries; i++) {
            try {
                UserRepresentation teacherUser = keycloakService.findByUsername("teacheruser");

                if (teacherUser != null) {
                    if (lecturerRepository.findByKeycloakId(teacherUser.getId()).isEmpty()) {
                        Lecturer lecturer = new Lecturer(teacherUser.getId());
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
                    }
                }

                UserRepresentation studentUser = keycloakService.findByUsername("studentuser");
                if (studentUser != null) {
                    if (studentRepository.findByKeycloakId(studentUser.getId()).isEmpty()) {
                        Student student = new Student(studentUser.getId());
                        studentRepository.save(student);
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
