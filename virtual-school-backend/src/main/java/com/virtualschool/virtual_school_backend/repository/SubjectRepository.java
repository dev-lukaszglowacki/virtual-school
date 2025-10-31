package com.virtualschool.virtual_school_backend.repository;

import com.virtualschool.virtual_school_backend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
