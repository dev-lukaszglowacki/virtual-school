package com.virtualschool.virtual_school_backend.repository;

import com.virtualschool.virtual_school_backend.model.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
}
