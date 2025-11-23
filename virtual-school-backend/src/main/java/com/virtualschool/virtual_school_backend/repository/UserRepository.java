package com.virtualschool.virtual_school_backend.repository;

import com.virtualschool.virtual_school_backend.model.Role;
import com.virtualschool.virtual_school_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
    List<User> findByRole(Role role);
}
