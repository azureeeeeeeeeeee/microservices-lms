package com.cendekia.enrollment_service.dtos.requests;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEnrollmentRequestDTO {
    @NotNull(message = "courseId is required")
    private UUID courseId;

    @NotNull(message = "userId is required")
    private UUID userId;
}
