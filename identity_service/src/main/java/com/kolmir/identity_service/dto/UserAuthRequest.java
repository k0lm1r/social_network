package com.kolmir.identity_service.dto;


public record UserAuthRequest (
    String username,
    String password
) {}