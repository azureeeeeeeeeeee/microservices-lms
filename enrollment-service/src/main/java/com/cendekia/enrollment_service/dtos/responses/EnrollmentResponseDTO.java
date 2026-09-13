package com.cendekia.enrollment_service.dtos.responses;

import com.cendekia.enrollment_service.dtos.EnrollmentDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentResponseDTO {
    private String message;
    private EnrollmentDTO enrollment;
}
