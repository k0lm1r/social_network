description = "Single join point"

dependencies {
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
	implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
	implementation("org.springframework.grpc:spring-grpc-client-spring-boot-starter")
	implementation(project(":proto"))
}