package com.virtualschool.virtual_school_backend.dto;

import java.util.Set;

public class StudentGroupDTO {
    private Long id;
    private String name;
    private Set<UserDTO> students;

    public StudentGroupDTO() {
    }

    public StudentGroupDTO(Long id, String name, Set<UserDTO> students) {
        this.id = id;
        this.name = name;
        this.students = students;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserDTO> getStudents() {
        return students;
    }

    public void setStudents(Set<UserDTO> students) {
        this.students = students;
    }
}