package com.cendekia.enrollment_service.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cendekia.enrollment_service.dtos.EnrollmentDTO;
import com.cendekia.enrollment_service.dtos.responses.ListEnrollmentsResponseDTO;
import com.cendekia.enrollment_service.exceptions.DuplicateEnrollmentException;
import com.cendekia.enrollment_service.exceptions.EnrollmentNotFoundException;
import com.cendekia.enrollment_service.exceptions.InvalidUserException;
import com.cendekia.enrollment_service.grpc.CourseGrpcClient;
import com.cendekia.enrollment_service.grpc.UserGrpcClient;
import com.cendekia.enrollment_service.mapper.EnrollmentMapper;
import com.cendekia.enrollment_service.models.Enrollment;
import com.cendekia.enrollment_service.repositories.EnrollmentRepository;
import com.cendekia.user.grpc.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserGrpcClient userGrpcClient;
    private final CourseGrpcClient courseGrpcClient;

    public EnrollmentDTO enrollStudent(UUID courseId, UUID userId) {
        UserResponse user = userGrpcClient.getUser(userId);

        if (!user.getRole().equals("STUDENT")) {
            throw new InvalidUserException("Only students can be enrolled in a course.");
        }

        // Validate course exists via gRPC call to course-service
        courseGrpcClient.getCourse(courseId);

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new DuplicateEnrollmentException(
                String.format("Student [ID: %s] is already enrolled in course [ID: %s]", userId, courseId)
            );
        }

        Enrollment enrollment = Enrollment.builder()
            .courseId(courseId)
            .userId(userId)
            .enrolledAt(Instant.now())
            .build();

        return EnrollmentMapper.toDTO(enrollmentRepository.save(enrollment));
    }

    public void unenrollStudent(UUID courseId, UUID userId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
            .orElseThrow(() -> new EnrollmentNotFoundException(
                String.format("Enrollment not found for student [ID: %s] in course [ID: %s]", userId, courseId)
            ));

        enrollmentRepository.delete(enrollment);
    }

    public ListEnrollmentsResponseDTO getEnrollmentsByStudent(UUID userId) {
        List<EnrollmentDTO> enrollments = enrollmentRepository.findByUserId(userId).stream()
            .map(EnrollmentMapper::toDTO)
            .toList();

        return ListEnrollmentsResponseDTO.builder()
            .message(String.format("Enrollments fetched for student [ID: %s]", userId))
            .enrollments(enrollments)
            .build();
    }

    public ListEnrollmentsResponseDTO getEnrollmentsByCourse(UUID courseId) {
        List<EnrollmentDTO> enrollments = enrollmentRepository.findByCourseId(courseId).stream()
            .map(EnrollmentMapper::toDTO)
            .toList();

        return ListEnrollmentsResponseDTO.builder()
            .message(String.format("Enrollments fetched for course [ID: %s]", courseId))
            .enrollments(enrollments)
            .build();
    }
}
