FROM gradle:9.3.1-jdk25 AS build

WORKDIR /app

COPY settings.gradle.kts .
COPY build.gradle.kts .

COPY proto/build.gradle.kts proto/build.gradle.kts
RUN gradle :proto:dependencies --no-daemon || true
COPY proto proto
RUN gradle :proto:build

COPY api_gateway/build.gradle.kts api_gateway/build.gradle.kts
RUN gradle :api_gateway:dependencies --no-daemon || true
COPY api_gateway api_gateway
RUN gradle :api_gateway:bootJar --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/api_gateway/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
