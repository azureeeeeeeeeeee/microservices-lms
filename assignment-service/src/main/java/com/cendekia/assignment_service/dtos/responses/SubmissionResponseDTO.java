package com.cendekia.assignment_service.dtos.responses;

import com.cendekia.assignment_service.dtos.SubmissionDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionResponseDTO {
    private String message;
    private SubmissionDTO submission;
}
