package com.cendekia.user_service.grpc;

import java.util.UUID;

import com.cendekia.user.grpc.GetUserByIdRequest;
import com.cendekia.user.grpc.UserResponse;
import com.cendekia.user.grpc.UserServiceGrpc.UserServiceImplBase;
import com.cendekia.user_service.models.User;
import com.cendekia.user_service.repositories.UserRepository;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceImplBase {
    private final UserRepository userRepository;

    @Override
    public void getUserById(
        GetUserByIdRequest request,
        StreamObserver<UserResponse> responseObserver
    ) {
        try {
            User user = userRepository.findById(UUID.fromString(request.getId()))
                    .orElse(null);

            if (user == null) {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription(String.format("User not found with ID: %s", request.getId()))
                        .asRuntimeException()
                );
                return;
            }
                    
            UserResponse response = UserResponse.newBuilder()
                                        .setId(user.getId().toString())
                                        .setFullname(user.getFullname())
                                        .setEmail(user.getEmail())
                                        .setRole(user.getRole().toString())
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
            log.error("Unexpected error fetching user: {}", request.getId(), e);
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal error while fetching user")
                    .asRuntimeException()
            );
        }
    }
}