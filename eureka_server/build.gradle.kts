plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

description = "Eureka server"

dependencies {
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
}

