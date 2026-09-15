package com.cendekia.assignment_service.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cendekia.assignment_service.dtos.AssignmentDTO;
import com.cendekia.assignment_service.dtos.SubmissionDTO;
import com.cendekia.assignment_service.dtos.requests.CreateAssignmentRequestDTO;
import com.cendekia.assignment_service.dtos.requests.GradeSubmissionRequestDTO;
import com.cendekia.assignment_service.dtos.requests.SubmitAssignmentRequestDTO;
import com.cendekia.assignment_service.dtos.requests.UpdateAssignmentRequestDTO;
import com.cendekia.assignment_service.dtos.responses.AssignmentResponseDTO;
import com.cendekia.assignment_service.dtos.responses.ListAssignmentsResponseDTO;
import com.cendekia.assignment_service.dtos.responses.SubmissionResponseDTO;
import com.cendekia.assignment_service.services.AssignmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    // ─── Assignment CUD ───────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<AssignmentResponseDTO> createAssignment(
        @Valid @RequestBody CreateAssignmentRequestDTO request,
        @RequestHeader("X-USER-ID") String userId,
        @RequestHeader("X-USER-ROLE") String userRole
    ) {
        AssignmentDTO assignment = assignmentService.createAssignment(request, userId, userRole);

        AssignmentResponseDTO response = new AssignmentResponseDTO();
        response.setMessage(String.format("Assignment created successfully [ID: %s]", assignment.getId()));
        response.setAssignment(assignment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResponseDTO> updateAssignment(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateAssignmentRequestDTO request,
        @RequestHeader("X-USER-ID") String userId,
        @RequestHeader("X-USER-ROLE") String userRole
    ) {
        AssignmentDTO assignment = assignmentService.updateAssignment(id, request, userId, userRole);

        AssignmentResponseDTO response = new AssignmentResponseDTO();
        response.setMessage(String.format("Assignment updated successfully [ID: %s]", assignment.getId()));
        response.setAssignment(assignment);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AssignmentResponseDTO> deleteAssignment(
        @PathVariable UUID id,
        @RequestHeader("X-USER-ID") String userId,
        @RequestHeader("X-USER-ROLE") String userRole
    ) {
        assignmentService.deleteAssignment(id, userId, userRole);

        AssignmentResponseDTO response = new AssignmentResponseDTO();
        response.setMessage(String.format("Assignment deleted successfully [ID: %s]", id));

        return ResponseEntity.ok(response);
    }

    // ─── Read assignments per course ──────────────────────────────────────────

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ListAssignmentsResponseDTO> getAssignmentsByCourse(
        @PathVariable UUID courseId,
        @RequestHeader("X-USER-ID") String userId,
        @RequestHeader("X-USER-ROLE") String userRole
    ) {
        ListAssignmentsResponseDTO response = assignmentService.getAssignmentsByCourse(courseId, userId, userRole);
        return ResponseEntity.ok(response);
    }

    // ─── Submit assignment ────────────────────────────────────────────────────

    @PostMapping("/{id}/submit")
    public ResponseEntity<SubmissionResponseDTO> submitAssignment(
        @PathVariable UUID id,
        @Valid @RequestBody SubmitAssignmentRequestDTO request,
        @RequestHeader("X-USER-ID") String userId,
        @RequestHeader("X-USER-ROLE") String userRole
    ) {
        SubmissionDTO submission = assignmentService.submitAssignment(id, request, userId, userRole);

        SubmissionResponseDTO response = new SubmissionResponseDTO();
        response.setMessage(String.format("Assignment [ID: %s] submitted successfully", id));
        response.setSubmission(submission);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─── View submission ──────────────────────────────────────────────────────

    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<SubmissionResponseDTO> getSubmission(
        @PathVariable UUID submissionId,
        @RequestHeader("X-USER-ID") String userId,
        @RequestHeader("X-USER-ROLE") String userRole
    ) {
        SubmissionDTO submission = assignmentService.getSubmission(submissionId, userId, userRole);

        SubmissionResponseDTO response = new SubmissionResponseDTO();
        response.setMessage(String.format("Submission fetched [ID: %s]", submissionId));
        response.setSubmission(submission);

        return ResponseEntity.ok(response);
    }

    // ─── Grade submission ─────────────────────────────────────────────────────

    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<SubmissionResponseDTO> gradeSubmission(
        @PathVariable UUID submissionId,
        @Valid @RequestBody GradeSubmissionRequestDTO request,
        @RequestHeader("X-USER-ID") String userId,
        @RequestHeader("X-USER-ROLE") String userRole
    ) {
        SubmissionDTO submission = assignmentService.gradeSubmission(submissionId, request, userId, userRole);

        SubmissionResponseDTO response = new SubmissionResponseDTO();
        response.setMessage(String.format("Submission [ID: %s] graded with score: %d", submissionId, submission.getScore()));
        response.setSubmission(submission);

        return ResponseEntity.ok(response);
    }
}
