package com.cendekia.assignment_service.dtos.responses;

import java.util.List;

import com.cendekia.assignment_service.dtos.SubmissionDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ListSubmissionsResponseDTO {
    private String message;
    private List<SubmissionDTO> submissions;
}
