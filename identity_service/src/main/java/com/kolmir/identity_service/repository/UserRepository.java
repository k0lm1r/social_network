package com.kolmir.identity_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.identity_service.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public boolean existsByIdAndKeycloakId(Long id, String keycloakId);
    public boolean existsById(Long id);
    public Optional<User> findByUsernameIgnoreCase(String username);
    public Optional<User> findByEmailIgnoreCase(String email);
    public User getUserByKeycloakId(String keycloakId);
}
