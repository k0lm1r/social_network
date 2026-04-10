package com.kolmir.api_gateway.logger;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GatewayLogger {
    public static void logRoute(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    
        String routeId = route != null ? route.getId() : "unknown";
        String targetUri = route != null ? route.getUri().toString() : "unknown";
    
        Integer status = exchange.getResponse().getStatusCode() != null
                ? exchange.getResponse().getStatusCode().value()
                : null;
    
        log.info(
            "traceId={} method={} path={} routeId={} targetUri={} status={}",
            exchange.getRequest().getId(),
            exchange.getRequest().getMethod(),
            exchange.getRequest().getURI().getPath(),
            routeId,
            targetUri,
            status
        );
    }
}
