package com.cendekia.user_service.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PasswordDoNotMatchException.class)
    public ResponseEntity<ApiError> handlePasswordDoNotMatchException(PasswordDoNotMatchException ex) {
        log.warn("Password do not match");
        ApiError apiError = new ApiError();
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ApiError apiError = new ApiError();
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setError("Validation error");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        apiError.setErrors(errors);
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("User not found for given UID");
        ApiError error = new ApiError();
        error.setError(ex.getMessage());

        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEMailAlreadyExistsException(EmailAlreadyExistsException ex) {
        log.warn("Email already exists (Ignoring current user)");
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setError(ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.warn("Given credentials is invalid");
        ApiError error = new ApiError();
        error.setError(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        log.warn("Given refresh token is invalid");
        ApiError error = new ApiError();
        error.setError(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
