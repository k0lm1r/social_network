package com.kolmir.identity_service.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.kolmir.identity_service.dto.ErrorResponse;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(AlreadyExistsException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorResponse.getExceptionMessage(e.getBindingResult()), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({JwtException.class, AuthenticationException.class, OAuth2AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(AccessDeniedException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
