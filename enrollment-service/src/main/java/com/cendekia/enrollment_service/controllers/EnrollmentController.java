package com.cendekia.enrollment_service.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cendekia.enrollment_service.dtos.EnrollmentDTO;
import com.cendekia.enrollment_service.dtos.requests.CreateEnrollmentRequestDTO;
import com.cendekia.enrollment_service.dtos.responses.EnrollmentResponseDTO;
import com.cendekia.enrollment_service.dtos.responses.ListEnrollmentsResponseDTO;
import com.cendekia.enrollment_service.services.EnrollmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enrollStudent(
        @Valid @RequestBody CreateEnrollmentRequestDTO request
    ) {
        EnrollmentDTO enrollment = enrollmentService.enrollStudent(
            request.getCourseId(),
            request.getUserId()
        );

        EnrollmentResponseDTO response = new EnrollmentResponseDTO();
        response.setMessage(String.format(
            "Student [ID: %s] enrolled in course [ID: %s]",
            enrollment.getUserId(),
            enrollment.getCourseId()
        ));
        response.setEnrollment(enrollment);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<EnrollmentResponseDTO> unenrollStudent(
        @RequestParam UUID courseId,
        @RequestParam UUID userId
    ) {
        enrollmentService.unenrollStudent(courseId, userId);

        EnrollmentResponseDTO response = new EnrollmentResponseDTO();
        response.setMessage(String.format(
            "Student [ID: %s] unenrolled from course [ID: %s]",
            userId,
            courseId
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/students/{userId}")
    public ResponseEntity<ListEnrollmentsResponseDTO> getEnrollmentsByStudent(
        @PathVariable UUID userId
    ) {
        ListEnrollmentsResponseDTO response = enrollmentService.getEnrollmentsByStudent(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ListEnrollmentsResponseDTO> getEnrollmentsByCourse(
        @PathVariable UUID courseId
    ) {
        ListEnrollmentsResponseDTO response = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(response);
    }
}
