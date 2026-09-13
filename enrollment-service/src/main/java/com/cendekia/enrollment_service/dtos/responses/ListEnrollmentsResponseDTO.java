package com.cendekia.enrollment_service.dtos.responses;

import java.util.List;

import com.cendekia.enrollment_service.dtos.EnrollmentDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ListEnrollmentsResponseDTO {
    private String message;
    private List<EnrollmentDTO> enrollments;
}
