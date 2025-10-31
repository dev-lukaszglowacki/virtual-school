package com.virtualschool.virtual_school_backend.repository;

import com.virtualschool.virtual_school_backend.model.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    @Query("SELECT sg FROM StudentGroup sg LEFT JOIN FETCH sg.students")
    List<StudentGroup> findAllWithStudents();
}
