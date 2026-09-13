package com.cendekia.course_service.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cendekia.course.grpc.CourseResponse;
import com.cendekia.course.grpc.GetCourseByIdRequest;
import com.cendekia.course_service.models.Course;
import com.cendekia.course_service.repositories.CourseRepository;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

@ExtendWith(MockitoExtension.class)
class CourseGrpcServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StreamObserver<CourseResponse> responseObserver;

    @InjectMocks
    private CourseGrpcService courseGrpcService;

    private UUID courseId;
    private UUID instructorId;

    @BeforeEach
    void setUp() {
        courseId = UUID.randomUUID();
        instructorId = UUID.randomUUID();
    }

    @Test
    void getCourseById_Success() {
        Course course = Course.builder()
                .id(courseId)
                .title("Fullstack Java Microservices")
                .description("Detailed description that satisfies validation constraints")
                .instructorId(instructorId)
                .createdBy(instructorId)
                .createdAt(Instant.now())
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        GetCourseByIdRequest request = GetCourseByIdRequest.newBuilder()
                .setId(courseId.toString())
                .build();

        courseGrpcService.getCourseById(request, responseObserver);

        ArgumentCaptor<CourseResponse> captor = ArgumentCaptor.forClass(CourseResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        CourseResponse response = captor.getValue();
        assertEquals(courseId.toString(), response.getId());
        assertEquals("Fullstack Java Microservices", response.getTitle());
        assertEquals(instructorId.toString(), response.getInstructorId());
    }

    @Test
    void getCourseById_NotFound() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        GetCourseByIdRequest request = GetCourseByIdRequest.newBuilder()
                .setId(courseId.toString())
                .build();

        courseGrpcService.getCourseById(request, responseObserver);

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(captor.capture());

        StatusRuntimeException error = (StatusRuntimeException) captor.getValue();
        assertEquals(Status.Code.NOT_FOUND, error.getStatus().getCode());
    }

    @Test
    void getCourseById_InvalidUUID() {
        GetCourseByIdRequest request = GetCourseByIdRequest.newBuilder()
                .setId("invalid-uuid")
                .build();

        courseGrpcService.getCourseById(request, responseObserver);

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(responseObserver).onError(captor.capture());

        StatusRuntimeException error = (StatusRuntimeException) captor.getValue();
        assertEquals(Status.Code.INVALID_ARGUMENT, error.getStatus().getCode());
    }
}
