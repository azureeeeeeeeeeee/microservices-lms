package com.cendekia.enrollment_service.dtos;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class EnrollmentDTO {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private Instant enrolledAt;
}
