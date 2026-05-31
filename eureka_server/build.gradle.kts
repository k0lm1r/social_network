plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

description = "Eureka server"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
}

