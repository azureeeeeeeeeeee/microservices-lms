package com.cendekia.assignment_service.mapper;

import com.cendekia.assignment_service.dtos.SubmissionDTO;
import com.cendekia.assignment_service.models.Submission;

public class SubmissionMapper {

    private SubmissionMapper() {}

    public static SubmissionDTO toDTO(Submission submission) {
        SubmissionDTO dto = new SubmissionDTO();
        dto.setId(submission.getId());
        dto.setAssignmentId(submission.getAssignment().getId());
        dto.setStudentId(submission.getStudentId());
        dto.setContent(submission.getContent());
        dto.setScore(submission.getScore());
        dto.setSubmittedAt(submission.getSubmittedAt());
        return dto;
    }
}
