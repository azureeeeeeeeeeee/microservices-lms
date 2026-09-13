package com.cendekia.course_service.grpc;

import java.util.UUID;

import com.cendekia.course.grpc.CourseResponse;
import com.cendekia.course.grpc.CourseServiceGrpc.CourseServiceImplBase;
import com.cendekia.course.grpc.GetCourseByIdRequest;
import com.cendekia.course_service.models.Course;
import com.cendekia.course_service.repositories.CourseRepository;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CourseGrpcService extends CourseServiceImplBase {
    private final CourseRepository courseRepository;

    @Override
    public void getCourseById(
        GetCourseByIdRequest request,
        StreamObserver<CourseResponse> responseObserver
    ) {
        try {
            Course course = courseRepository.findById(UUID.fromString(request.getId()))
                    .orElse(null);

            if (course == null) {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription(String.format("Course not found with ID: %s", request.getId()))
                        .asRuntimeException()
                );
                return;
            }

            CourseResponse response = CourseResponse.newBuilder()
                    .setId(course.getId().toString())
                    .setTitle(course.getTitle())
                    .setDescription(course.getDescription())
                    .setInstructorId(course.getInstructorId().toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", request.getId(), e);
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(String.format("Invalid UUID format: %s", request.getId()))
                    .asRuntimeException()
            );
        } catch (Exception e) {
            log.error("Unexpected error fetching course: {}", request.getId(), e);
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal error while fetching course")
                    .asRuntimeException()
            );
        }
    }
}
