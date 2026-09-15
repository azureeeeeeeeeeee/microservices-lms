package com.cendekia.assignment_service.clients;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentClient {

    private final RestClient enrollmentRestClient;

    /**
     * Returns true if the student is enrolled in the course.
     * Calls GET /enrollments/students/{userId} on enrollment-service
     * and checks if any enrollment matches the given courseId.
     */
    public boolean isEnrolled(UUID courseId, UUID userId) {
        try {
            Map<String, Object> response = enrollmentRestClient.get()
                    .uri("/enrollments/students/{userId}", userId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response == null) {
                return false;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> enrollments = (List<Map<String, Object>>) response.get("enrollments");

            if (enrollments == null) {
                return false;
            }

            return enrollments.stream()
                    .anyMatch(e -> courseId.toString().equals(e.get("courseId")));
        } catch (Exception ex) {
            log.error("Failed to check enrollment for student [{}] in course [{}]: {}", userId, courseId, ex.getMessage());
            return false;
        }
    }
}
