plugins {
	java
}

description = "Social interaction processing service: subscriptions, likes, dislikes"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-web")
	implementation(project(":logging-common"))
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
}
