plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "social-network"

include(
    "identity_service",
    "proto",
    "eureka_server"
)