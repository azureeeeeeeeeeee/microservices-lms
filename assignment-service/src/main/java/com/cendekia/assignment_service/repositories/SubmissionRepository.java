package com.cendekia.assignment_service.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cendekia.assignment_service.models.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findByAssignmentId(UUID assignmentId);

    List<Submission> findByStudentId(UUID studentId);

    Optional<Submission> findByAssignmentIdAndStudentId(UUID assignmentId, UUID studentId);

    boolean existsByAssignmentIdAndStudentId(UUID assignmentId, UUID studentId);
}
