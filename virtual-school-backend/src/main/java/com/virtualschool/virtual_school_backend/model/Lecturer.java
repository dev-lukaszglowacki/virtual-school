package com.virtualschool.virtual_school_backend.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keycloakId;

    public Lecturer() {
    }

    public Lecturer(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lecturer lecturer = (Lecturer) o;
        return Objects.equals(id, lecturer.id) && Objects.equals(keycloakId, lecturer.keycloakId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keycloakId);
    }

    @Override
    public String toString() {
        return "Lecturer{" +
               "id=" + id +
               ", keycloakId='" + keycloakId + '\'' +
               '}';
    }
}
