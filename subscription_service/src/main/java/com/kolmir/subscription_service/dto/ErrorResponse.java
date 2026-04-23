package com.kolmir.subscription_service.dto;

import java.time.LocalDateTime;


public record ErrorResponse (
    String message,
    LocalDateTime time
) {
    public ErrorResponse (String message) {
        this(message, LocalDateTime.now());
    }
}
