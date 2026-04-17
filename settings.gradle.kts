plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "social-network"

val modules = listOf("identity_service",
                    "proto",
                    "eureka_server",
                    "api_gateway",
                    "logging-common",
                    "subsciption_serv")

modules
    .filter { file(it).isDirectory }
    .forEach { include(":$it") }