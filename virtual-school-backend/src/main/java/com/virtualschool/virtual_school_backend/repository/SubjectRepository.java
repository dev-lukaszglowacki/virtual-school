package com.virtualschool.virtual_school_backend.repository;

import com.virtualschool.virtual_school_backend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByLecturerId(Long lecturerId);
    Subject findByName(String name);
}
