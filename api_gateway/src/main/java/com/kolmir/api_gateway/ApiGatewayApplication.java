package com.kolmir.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.grpc.client.ImportGrpcClients;


@EnableDiscoveryClient
@SpringBootApplication
@ImportGrpcClients(basePackages = "com.kolmir.validate_token")
public class ApiGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
}
