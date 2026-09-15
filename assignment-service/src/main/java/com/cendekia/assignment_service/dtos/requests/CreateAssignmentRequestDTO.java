package com.cendekia.assignment_service.dtos.requests;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAssignmentRequestDTO {
    @NotNull(message = "courseId is required")
    private UUID courseId;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "deadline is required")
    private Instant deadline;
}
