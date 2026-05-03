package com.kolmir.subscription_service.model;


public record CurrentUser (
    Long id,
    String role
) {}