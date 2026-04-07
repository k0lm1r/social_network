package com.kolmir.api_gateway.interceptor;

import io.grpc.*;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class ClientAuthInterceptor implements ClientInterceptor {

    private final String token;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Metadata.Key<String> AUTH_HEADER =
                        Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

                headers.put(AUTH_HEADER, token.contains("Bearer ") ? token : "Bearer " + token);

                super.start(responseListener, headers);
            }
        };
    }
    
}
