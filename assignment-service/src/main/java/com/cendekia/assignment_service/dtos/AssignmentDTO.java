package com.cendekia.assignment_service.dtos;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentDTO {
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private Instant deadline;
}
