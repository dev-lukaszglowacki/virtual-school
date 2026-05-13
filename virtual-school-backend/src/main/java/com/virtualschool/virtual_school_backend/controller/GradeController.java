package com.virtualschool.virtual_school_backend.controller;

import java.util.List;

import com.virtualschool.virtual_school_backend.dto.GradeDTO;
import com.virtualschool.virtual_school_backend.service.GradeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping
    public ResponseEntity<GradeDTO> addGrade(@RequestBody GradeDTO gradeDTO) {
        return gradeService.addGrade(gradeDTO);
    }

    @GetMapping("/student")
    public ResponseEntity<List<GradeDTO>> getStudentGrades() {
        return gradeService.getStudentGrades();
    }

    @GetMapping("/lecturer")
    public ResponseEntity<List<GradeDTO>> getLecturerGrades() {
        return gradeService.getLecturerGrades();
    }
    
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<GradeDTO>> getGradesForSubject(@PathVariable Long subjectId) {
        return gradeService.getGradesForSubject(subjectId);
    }
}
