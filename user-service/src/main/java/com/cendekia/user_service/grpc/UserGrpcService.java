package com.cendekia.user_service.grpc;

import java.util.UUID;

import com.cendekia.user.grpc.GetUserByIdRequest;
import com.cendekia.user.grpc.UserResponse;
import com.cendekia.user.grpc.UserServiceGrpc.UserServiceImplBase;
import com.cendekia.user_service.models.User;
import com.cendekia.user_service.repositories.UserRepository;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceImplBase {
    private final UserRepository userRepository;

    @Override
    public void getUserById(
        GetUserByIdRequest request,
        StreamObserver<UserResponse> responseObserver
    ) {
        User user = userRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        UserResponse  response = UserResponse.newBuilder()
                                    .setId(user.getId().toString())
                                    .setFullname(user.getFullname())
                                    .setEmail(user.getEmail())
                                    .setRole(user.getRole().toString())
                                    .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}