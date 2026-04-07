package com.kolmir.api_gateway.service;

import org.springframework.stereotype.Service;

import com.google.protobuf.Empty;
import com.kolmir.api_gateway.interceptor.ClientAuthInterceptor;
import com.kolmir.validate_token.UserResponse;
import com.kolmir.validate_token.ValidateTokenGrpc;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class TokenValidationService {
    private final ValidateTokenGrpc.ValidateTokenBlockingStub stub;

    public UserResponse getUserFromToken(String token) {
        ValidateTokenGrpc.ValidateTokenBlockingStub stubWithAuth = 
                stub.withInterceptors(new ClientAuthInterceptor(token));
        UserResponse response = stubWithAuth.getUser(Empty.newBuilder().build());
        return response;
    }
}
