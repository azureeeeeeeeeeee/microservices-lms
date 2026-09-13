package com.cendekia.enrollment_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cendekia.course.grpc.CourseResponse;
import com.cendekia.enrollment_service.dtos.EnrollmentDTO;
import com.cendekia.enrollment_service.exceptions.InvalidUserException;
import com.cendekia.enrollment_service.grpc.CourseGrpcClient;
import com.cendekia.enrollment_service.grpc.UserGrpcClient;
import com.cendekia.enrollment_service.models.Enrollment;
import com.cendekia.enrollment_service.repositories.EnrollmentRepository;
import com.cendekia.user.grpc.UserResponse;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserGrpcClient userGrpcClient;

    @Mock
    private CourseGrpcClient courseGrpcClient;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private UUID userId;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        courseId = UUID.randomUUID();
    }

    @Test
    void enrollStudent_Success() {
        UserResponse studentUser = UserResponse.newBuilder()
                .setId(userId.toString())
                .setRole("STUDENT")
                .setFullname("John Doe")
                .build();

        CourseResponse courseResponse = CourseResponse.newBuilder()
                .setId(courseId.toString())
                .setTitle("Intro to Microservices")
                .build();

        Enrollment savedEnrollment = Enrollment.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .courseId(courseId)
                .enrolledAt(Instant.now())
                .build();

        when(userGrpcClient.getUser(userId)).thenReturn(studentUser);
        when(courseGrpcClient.getCourse(courseId)).thenReturn(courseResponse);
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);

        EnrollmentDTO result = enrollmentService.enrollStudent(courseId, userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(courseId, result.getCourseId());
        verify(courseGrpcClient).getCourse(courseId);
    }

    @Test
    void enrollStudent_CourseNotFound_ThrowsException() {
        UserResponse studentUser = UserResponse.newBuilder()
                .setId(userId.toString())
                .setRole("STUDENT")
                .build();

        when(userGrpcClient.getUser(userId)).thenReturn(studentUser);
        when(courseGrpcClient.getCourse(courseId))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND.withDescription("Course not found")));

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> enrollmentService.enrollStudent(courseId, userId)
        );

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode());
        verify(courseGrpcClient).getCourse(courseId);
    }

    @Test
    void enrollStudent_NonStudentUser_ThrowsInvalidUserException() {
        UserResponse teacherUser = UserResponse.newBuilder()
                .setId(userId.toString())
                .setRole("TEACHER")
                .build();

        when(userGrpcClient.getUser(userId)).thenReturn(teacherUser);

        assertThrows(
                InvalidUserException.class,
                () -> enrollmentService.enrollStudent(courseId, userId)
        );
    }
}
