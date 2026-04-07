package com.kolmir.identity_service.grpc;

import org.springframework.grpc.server.service.GrpcService;

import com.google.protobuf.Empty;
import com.kolmir.identity_service.mapper.UserMapper;
import com.kolmir.identity_service.service.SecurityService;
import com.kolmir.validate_token.UserResponse;
import com.kolmir.validate_token.ValidateTokenGrpc.ValidateTokenImplBase;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;


@GrpcService
@RequiredArgsConstructor
public class ValidateToken extends ValidateTokenImplBase {
    private final UserMapper userMapper;
    private final SecurityService securityService;

    @Override
    public void getUser(Empty request, StreamObserver<UserResponse> responseObserver) {
        UserResponse response = userMapper.toGrpcUserResponse(securityService.getUserFromPrincipal());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
