plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "social-network"

val modules = listOf("identity_service",
                    "proto",
                    "eureka_server",
                    "api_gateway",
                    "logging-common",
                    "subscription_service")

modules
    .filter { file(it).isDirectory }
    .forEach { include(":$it") }

include(":auth-contract")
project(":auth-contract").projectDir = file("common/auth-contract")

include(":auth-spring-security")
project(":auth-spring-security").projectDir = file("common/auth-spring-security")