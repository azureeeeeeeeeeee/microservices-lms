package com.cendekia.enrollment_service.mapper;

import com.cendekia.enrollment_service.dtos.EnrollmentDTO;
import com.cendekia.enrollment_service.models.Enrollment;

public class EnrollmentMapper {

    private EnrollmentMapper() {}

    public static EnrollmentDTO toDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setUserId(enrollment.getUserId());
        dto.setCourseId(enrollment.getCourseId());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        return dto;
    }
}
