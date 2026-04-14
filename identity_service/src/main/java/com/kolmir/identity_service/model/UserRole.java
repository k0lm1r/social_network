package com.kolmir.identity_service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER"),
    MAIN_ADMIN("MAIN_ADMIN");

    private final String stringName;
}
