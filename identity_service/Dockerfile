FROM gradle:9.3.1-jdk25 AS build
WORKDIR /app

COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY identity_service/build.gradle.kts identity_service/build.gradle.kts
COPY proto/build.gradle.kts proto/build.gradle.kts
COPY common/logging-common/build.gradle.kts common/logging-common/build.gradle.kts
COPY common/auth-contract/build.gradle.kts common/auth-contract/build.gradle.kts

RUN gradle :identity_service:dependencies --no-daemon || true

COPY identity_service identity_service
COPY common/logging-common common/logging-common
COPY common/auth-contract common/auth-contract
COPY proto proto

RUN gradle :identity_service:bootJar -x test --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/identity_service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
