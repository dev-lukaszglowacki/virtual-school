package com.virtualschool.virtual_school_backend.controller;

import com.virtualschool.virtual_school_backend.model.Lecturer;
import com.virtualschool.virtual_school_backend.repository.LecturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturers")
public class LecturerController {

    @Autowired
    private LecturerRepository lecturerRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'teacher')")
    public ResponseEntity<Lecturer> getLecturerById(@PathVariable Long id) {
        return lecturerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public Lecturer createLecturer(@RequestBody Lecturer lecturer) {
        return lecturerRepository.save(lecturer);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Lecturer> updateLecturer(@PathVariable Long id, @RequestBody Lecturer lecturerDetails) {
        return lecturerRepository.findById(id)
                .map(lecturer -> {
                    lecturer.setFirstName(lecturerDetails.getFirstName());
                    lecturer.setLastName(lecturerDetails.getLastName());
                    lecturer.setEmail(lecturerDetails.getEmail());
                    lecturer.setSubject(lecturerDetails.getSubject());
                    return ResponseEntity.ok(lecturerRepository.save(lecturer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Long id) {
        return lecturerRepository.findById(id)
                .map(lecturer -> {
                    lecturerRepository.delete(lecturer);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
