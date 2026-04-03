plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "social-network"

val modules = listOf("identity_service",
                    "proto",
                    "eureka_server",
                    "api_gateway")

modules
    .filter { file(it).isDirectory }
    .forEach { include(":$it") }