package com.cendekia.course_service.dtos.requests;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter 
@Getter 
public class AssignInstructorRequestDTO {
    @NotNull(message = "New Instructor ID is required")
    private UUID newInstructorId;
}
