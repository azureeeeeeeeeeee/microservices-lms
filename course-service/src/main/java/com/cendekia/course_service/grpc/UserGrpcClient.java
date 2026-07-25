package com.cendekia.course_service.grpc;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cendekia.user.grpc.GetUserByIdRequest;
import com.cendekia.user.grpc.UserResponse;
import com.cendekia.user.grpc.UserServiceGrpc.UserServiceBlockingStub;

import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class UserGrpcClient {
    @GrpcClient("user-service")
    private UserServiceBlockingStub stub;

    public UserResponse getUser(UUID id) {

        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                                        .setId(id.toString())
                                        .build();

        return stub.getUserById(request);
    }
}
