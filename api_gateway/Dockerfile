FROM gradle:9.3.1-jdk25 AS build

WORKDIR /app

COPY settings.gradle.kts .
COPY build.gradle.kts .

COPY api_gateway/build.gradle.kts api_gateway/build.gradle.kts
COPY proto/build.gradle.kts proto/build.gradle.kts
COPY logging-common/build.gradle.kts logging-common/build.gradle.kts
RUN gradle :api_gateway:dependencies --no-daemon || true

COPY proto proto
COPY api_gateway api_gateway
COPY logging-common logging-common
RUN gradle :api_gateway:bootJar --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/api_gateway/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
