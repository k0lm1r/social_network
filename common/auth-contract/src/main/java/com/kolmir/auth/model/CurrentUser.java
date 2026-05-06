package com.kolmir.auth.model;


public record CurrentUser (
    Long id,
    String username,
    String email,
    UserRole role
) {}