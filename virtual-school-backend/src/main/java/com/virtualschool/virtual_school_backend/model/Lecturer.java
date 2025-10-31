package com.virtualschool.virtual_school_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String subject;

    public Lecturer() {
    }

    public Lecturer(String firstName, String lastName, String email, String subject) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lecturer lecturer = (Lecturer) o;
        return Objects.equals(id, lecturer.id) && Objects.equals(firstName, lecturer.firstName) && Objects.equals(lastName, lecturer.lastName) && Objects.equals(email, lecturer.email) && Objects.equals(subject, lecturer.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, subject);
    }

    @Override
    public String toString() {
        return "Lecturer{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", subject='" + subject + '\'' +
               '}';
    }
}
