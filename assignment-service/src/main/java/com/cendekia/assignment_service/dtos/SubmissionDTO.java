package com.cendekia.assignment_service.dtos;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionDTO {
    private UUID id;
    private UUID assignmentId;
    private UUID studentId;
    private String content;
    private Integer score;
    private Instant submittedAt;
}
