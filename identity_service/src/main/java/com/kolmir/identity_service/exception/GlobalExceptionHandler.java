package com.kolmir.identity_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kolmir.identity_service.dto.exception.ErrorResponse;

import static com.kolmir.identity_service.util.KeycloakConstants.*;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleException(AlreadyExistsException e) {
        ErrorResponse errorResponse = getErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({NotFoundException.class, jakarta.ws.rs.NotFoundException.class})
    public ResponseEntity<ErrorResponse> handleException(NotFoundException e) {
        ErrorResponse errorResponse = getErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BadRequestException.class})
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorResponse.getExceptionMessage(e.getBindingResult()), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({JwtException.class, AuthenticationException.class, 
        OAuth2AuthenticationException.class, NotAuthorizedException.class
    })
    public ResponseEntity<ErrorResponse> handleException(RuntimeException e) {
        ErrorResponse errorResponse = getErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(AccessDeniedException e) {
        ErrorResponse errorResponse = getErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ChangingForbidenException.class, ForbiddenException.class})
    public ResponseEntity<ErrorResponse> handleException(ChangingForbidenException e) {
        ErrorResponse errorResponse = getErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleException(HttpClientErrorException e) {
        HttpStatusCode status = e.getStatusCode();
        String message;
        if (status.value() == HttpStatus.UNAUTHORIZED.value()) {
            message = UNAUTHORIZED_EXCEPTION_MESSAGE;
        } else if (e.getResponseBodyAsString().contains(INVALID_GRANT)) {
            Map<String, Object> response = getErrorBody(e.getResponseBodyAsString());
            message = response.get(ERROR_DESCRIPTION).toString();
        } else 
            message = e.getResponseBodyAsString();
        return new ResponseEntity<>(getErrorResponse(message), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = getErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse getErrorResponse(String message) {
        return new ErrorResponse(message, LocalDateTime.now());
    }

    private Map<String, Object> getErrorBody(String errorBody) {
        ObjectMapper mapper = new JsonMapper();
        try {
            return mapper.readValue(errorBody, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}
