package com.kolmir.identity_service.dto.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;


public record ErrorResponse (
    String message,
    LocalDateTime timestamp
) {
    public static String getExceptionMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + " - " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }
}
