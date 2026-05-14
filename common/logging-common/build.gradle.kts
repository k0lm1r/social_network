plugins {
    `java-library`
}

description = "Shared Spring Logging module: logging aspect + autoconfiguration"

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
}