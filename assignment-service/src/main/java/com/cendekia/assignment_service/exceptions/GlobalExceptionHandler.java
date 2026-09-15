package com.cendekia.assignment_service.exceptions;

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

    @ExceptionHandler(AssignmentNotFoundException.class)
    public ResponseEntity<ApiError> handleAssignmentNotFoundException(AssignmentNotFoundException ex) {
        log.warn("Assignment not found: {}", ex.getMessage());
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SubmissionNotFoundException.class)
    public ResponseEntity<ApiError> handleSubmissionNotFoundException(SubmissionNotFoundException ex) {
        log.warn("Submission not found: {}", ex.getMessage());
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AlreadySubmittedException.class)
    public ResponseEntity<ApiError> handleAlreadySubmittedException(AlreadySubmittedException ex) {
        log.warn("Duplicate submission: {}", ex.getMessage());
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ApiError apiError = new ApiError();
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setError("Validation error");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
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
