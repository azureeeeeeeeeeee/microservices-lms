package com.cendekia.enrollment_service.grpc;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cendekia.course.grpc.CourseResponse;
import com.cendekia.course.grpc.CourseServiceGrpc.CourseServiceBlockingStub;
import com.cendekia.course.grpc.GetCourseByIdRequest;

import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class CourseGrpcClient {
    @GrpcClient("course-service")
    private CourseServiceBlockingStub stub;

    public CourseResponse getCourse(UUID id) {
        GetCourseByIdRequest request = GetCourseByIdRequest.newBuilder()
                .setId(id.toString())
                .build();

        return stub.getCourseById(request);
    }
}
