package com.cendekia.assignment_service.dtos.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeSubmissionRequestDTO {
    @NotNull(message = "score is required")
    @Min(value = 0, message = "score must be between 0 and 100")
    @Max(value = 100, message = "score must be between 0 and 100")
    private Integer score;
}
