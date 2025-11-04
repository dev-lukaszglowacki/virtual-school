package com.virtualschool.virtual_school_backend.repository;

import com.virtualschool.virtual_school_backend.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByKeycloakId(String keycloakId);
}