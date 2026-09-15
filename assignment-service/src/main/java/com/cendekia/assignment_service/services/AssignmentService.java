package com.cendekia.assignment_service.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cendekia.assignment_service.dtos.AssignmentDTO;
import com.cendekia.assignment_service.dtos.SubmissionDTO;
import com.cendekia.assignment_service.dtos.requests.CreateAssignmentRequestDTO;
import com.cendekia.assignment_service.dtos.requests.GradeSubmissionRequestDTO;
import com.cendekia.assignment_service.dtos.requests.SubmitAssignmentRequestDTO;
import com.cendekia.assignment_service.dtos.requests.UpdateAssignmentRequestDTO;
import com.cendekia.assignment_service.dtos.responses.ListAssignmentsResponseDTO;
import com.cendekia.assignment_service.exceptions.AlreadySubmittedException;
import com.cendekia.assignment_service.exceptions.AssignmentNotFoundException;
import com.cendekia.assignment_service.exceptions.SubmissionNotFoundException;
import com.cendekia.assignment_service.grpc.CourseGrpcClient;
import com.cendekia.assignment_service.mapper.AssignmentMapper;
import com.cendekia.assignment_service.mapper.SubmissionMapper;
import com.cendekia.assignment_service.models.Assignment;
import com.cendekia.assignment_service.models.Submission;
import com.cendekia.assignment_service.permissions.AssignmentPermissions;
import com.cendekia.assignment_service.repositories.AssignmentRepository;
import com.cendekia.assignment_service.repositories.SubmissionRepository;
import com.cendekia.course.grpc.CourseResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseGrpcClient courseGrpcClient;
    private final AssignmentPermissions assignmentPermissions;

    // ─── Assignment CUD ───────────────────────────────────────────────────────

    public AssignmentDTO createAssignment(CreateAssignmentRequestDTO request, String userId, String role) {
        CourseResponse course = courseGrpcClient.getCourse(request.getCourseId());
        assignmentPermissions.checkManageAssignment(role, userId, course);

        Assignment assignment = Assignment.builder()
                .courseId(request.getCourseId())
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .build();

        return AssignmentMapper.toDTO(assignmentRepository.save(assignment));
    }

    public AssignmentDTO updateAssignment(UUID id, UpdateAssignmentRequestDTO request, String userId, String role) {
        Assignment assignment = findAssignmentOrThrow(id);
        CourseResponse course = courseGrpcClient.getCourse(assignment.getCourseId());
        assignmentPermissions.checkManageAssignment(role, userId, course);

        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDeadline(request.getDeadline());

        return AssignmentMapper.toDTO(assignmentRepository.save(assignment));
    }

    public void deleteAssignment(UUID id, String userId, String role) {
        Assignment assignment = findAssignmentOrThrow(id);
        CourseResponse course = courseGrpcClient.getCourse(assignment.getCourseId());
        assignmentPermissions.checkManageAssignment(role, userId, course);

        assignmentRepository.delete(assignment);
    }

    // ─── Read assignments per course ──────────────────────────────────────────

    public ListAssignmentsResponseDTO getAssignmentsByCourse(UUID courseId, String userId, String role) {
        // Validate course exists
        courseGrpcClient.getCourse(courseId);

        assignmentPermissions.checkReadAssignments(role, userId, courseId);

        List<AssignmentDTO> assignments = assignmentRepository.findByCourseId(courseId).stream()
                .map(AssignmentMapper::toDTO)
                .toList();

        return ListAssignmentsResponseDTO.builder()
                .message(String.format("Assignments fetched for course [ID: %s]", courseId))
                .assignments(assignments)
                .build();
    }

    // ─── Submit assignment ────────────────────────────────────────────────────

    public SubmissionDTO submitAssignment(UUID assignmentId, SubmitAssignmentRequestDTO request, String userId, String role) {
        Assignment assignment = findAssignmentOrThrow(assignmentId);

        assignmentPermissions.checkSubmit(role, userId, assignment.getCourseId());

        UUID studentId = UUID.fromString(userId);

        if (submissionRepository.existsByAssignmentIdAndStudentId(assignmentId, studentId)) {
            throw new AlreadySubmittedException(
                    String.format("Student [ID: %s] has already submitted assignment [ID: %s]", userId, assignmentId)
            );
        }

        Submission submission = Submission.builder()
                .assignment(assignment)
                .studentId(studentId)
                .content(request.getContent())
                .submittedAt(Instant.now())
                .build();

        return SubmissionMapper.toDTO(submissionRepository.save(submission));
    }

    // ─── View submission ──────────────────────────────────────────────────────

    public SubmissionDTO getSubmission(UUID submissionId, String userId, String role) {
        Submission submission = findSubmissionOrThrow(submissionId);
        CourseResponse course = courseGrpcClient.getCourse(submission.getAssignment().getCourseId());

        assignmentPermissions.checkViewSubmission(role, userId, submission, course);

        return SubmissionMapper.toDTO(submission);
    }

    // ─── Grade submission ─────────────────────────────────────────────────────

    public SubmissionDTO gradeSubmission(UUID submissionId, GradeSubmissionRequestDTO request, String userId, String role) {
        Submission submission = findSubmissionOrThrow(submissionId);
        CourseResponse course = courseGrpcClient.getCourse(submission.getAssignment().getCourseId());

        assignmentPermissions.checkGrade(role, userId, course);

        submission.setScore(request.getScore());

        return SubmissionMapper.toDTO(submissionRepository.save(submission));
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private Assignment findAssignmentOrThrow(UUID id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException(
                        String.format("Assignment not found [ID: %s]", id)
                ));
    }

    private Submission findSubmissionOrThrow(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new SubmissionNotFoundException(
                        String.format("Submission not found [ID: %s]", id)
                ));
    }
}
