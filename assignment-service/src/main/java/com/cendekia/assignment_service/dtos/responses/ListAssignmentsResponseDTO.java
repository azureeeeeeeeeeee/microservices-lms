package com.cendekia.assignment_service.dtos.responses;

import java.util.List;

import com.cendekia.assignment_service.dtos.AssignmentDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ListAssignmentsResponseDTO {
    private String message;
    private List<AssignmentDTO> assignments;
}
