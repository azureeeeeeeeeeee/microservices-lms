package com.cendekia.assignment_service.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAssignmentRequestDTO {
    @NotBlank(message = "content is required")
    private String content;
}
