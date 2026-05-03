package com.kolmir.identity_service.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@ToString
@Getter @Setter
@EqualsAndHashCode
@Table(name = "users")
public class User {
    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @EqualsAndHashCode.Exclude
    @Column(name = "keycloak_id")
    private String keycloakId;

    private String email;
    private String username;

    @Column(name = "display_name")
    private String displayName;

    private String bio;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
}
