package com.cendekia.assignment_service.mapper;

import com.cendekia.assignment_service.dtos.AssignmentDTO;
import com.cendekia.assignment_service.models.Assignment;

public class AssignmentMapper {

    private AssignmentMapper() {}

    public static AssignmentDTO toDTO(Assignment assignment) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setCourseId(assignment.getCourseId());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setDeadline(assignment.getDeadline());
        return dto;
    }
}
