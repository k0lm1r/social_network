package com.kolmir.feed_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kolmir.feed_service.dto.exception.ErrorResponse;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler (NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> exceptionHandler (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorResponse(e.getMessage()));
    }

    private ErrorResponse getErrorResponse (String message) {
        return new ErrorResponse(message);
    }
}
