package com.kolmir.feed_service.dto.exception;

import java.time.LocalDateTime;


public record ErrorResponse (
    String message,
    LocalDateTime time
) {
    public ErrorResponse (String message) {
        this(message, LocalDateTime.now());
    }
}
