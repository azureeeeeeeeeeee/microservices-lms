package com.cendekia.assignment_service.dtos.responses;

import com.cendekia.assignment_service.dtos.AssignmentDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentResponseDTO {
    private String message;
    private AssignmentDTO assignment;
}
