package com.virtualschool.virtual_school_backend.repository;

import com.virtualschool.virtual_school_backend.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findByLecturerId(Long lecturerId);
    List<Grade> findBySubjectId(Long subjectId);
}
