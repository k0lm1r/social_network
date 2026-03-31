package com.kolmir.identity_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kolmir.identity_service.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public boolean existsByIdAndKeycloakId(Long id, String keycloakId);
    public boolean existsByKeycloakId(UUID keycloakId);
}
