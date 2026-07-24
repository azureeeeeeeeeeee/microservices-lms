package com.cendekia.course_service.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(InvalidCourseException.class)
    public ResponseEntity<ApiError> handleInvalidCourseException(InvalidCourseException ex) {
        log.warn("Invalid Course Given");
        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setError(ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
