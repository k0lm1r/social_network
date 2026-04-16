package com.kolmir.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.kolmir.api_gateway.filter.util.HeaderSetter;
import com.kolmir.api_gateway.logger.GatewayLogger;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.kolmir.api_gateway.util.FilterConstants.*;

import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidationFilter implements GlobalFilter, Ordered {
    private final HeaderSetter headerSetter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String auth = exchange.getRequest().getHeaders().getFirst(AUTORIZATION_HEADER);

        if (auth != null && !auth.isBlank()) {
            try {
                var request = headerSetter.addUserData(exchange.getRequest(), auth);
                return chain.filter(exchange.mutate().request(request).build());
            } catch (StatusRuntimeException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        GatewayLogger.logRoute(exchange);
        return chain.filter(exchange);
    }
    
    @Override
    public int getOrder() {
        return -1;
    }
}
