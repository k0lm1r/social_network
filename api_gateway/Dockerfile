FROM gradle:9.3.1-jdk25 AS build

WORKDIR /app

COPY settings.gradle.kts .
COPY build.gradle.kts .

COPY api_gateway/build.gradle.kts api_gateway/build.gradle.kts
COPY proto/build.gradle.kts proto/build.gradle.kts
COPY common/logging-common/build.gradle.kts common/logging-common/build.gradle.kts
COPY common/auth-contract/build.gradle.kts common/auth-contract/build.gradle.kts
RUN gradle :api_gateway:dependencies --no-daemon || true

COPY proto proto
COPY api_gateway api_gateway
COPY common/logging-common common/logging-common
COPY common/auth-contract common/auth-contract
RUN gradle :api_gateway:bootJar -x test --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/api_gateway/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
