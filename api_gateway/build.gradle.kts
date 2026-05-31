description = "Single join point"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
	implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
	implementation("org.springframework.grpc:spring-grpc-client-spring-boot-starter")
	implementation(project(":proto"))
	implementation(project(":logging-common"))
	implementation(project(":auth-contract"))
	implementation("org.springframework.boot:spring-boot-starter-aspectj")

	testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
