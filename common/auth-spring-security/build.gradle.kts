plugins {
    `java-library`
}

description = "Shared Spring Security module: user headers filter + autoconfiguration"

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

dependencies {
    api(project(":auth-contract"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework:spring-web")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}
