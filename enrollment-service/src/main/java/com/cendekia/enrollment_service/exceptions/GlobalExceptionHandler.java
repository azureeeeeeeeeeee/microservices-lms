package com.cendekia.enrollment_service.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ApiError> handleEnrollmentNotFoundException(EnrollmentNotFoundException ex) {
        log.warn("Enrollment not found: {}", ex.getMessage());
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateEnrollmentException.class)
    public ResponseEntity<ApiError> handleDuplicateEnrollmentException(DuplicateEnrollmentException ex) {
        log.warn("Duplicate enrollment: {}", ex.getMessage());
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ApiError> handleInvalidUserException(InvalidUserException ex) {
        log.warn("Invalid user: {}", ex.getMessage());
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(error);
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

    @ExceptionHandler(io.grpc.StatusRuntimeException.class)
    public ResponseEntity<ApiError> handleGrpcException(io.grpc.StatusRuntimeException ex) {
        log.error("gRPC call failed: {}", ex.getMessage());
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());

        HttpStatus httpStatus = switch (ex.getStatus().getCode()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        error.setError(ex.getStatus().getDescription() != null
            ? ex.getStatus().getDescription()
            : "An error occurred while communicating with an internal service");
        return ResponseEntity.status(httpStatus).body(error);
    }
}
