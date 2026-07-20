package com.cendekia.user_service.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApiError {
    private LocalDateTime timestamp;
    private String error;
    private Map<String, String> errors;
}
