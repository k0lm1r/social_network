plugins {
    `java-library`
}

description = "Shared auth contract: headers, roles, principal DTO"

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}
