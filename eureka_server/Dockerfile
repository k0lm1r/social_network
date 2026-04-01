FROM gradle:9.3.1-jdk25 AS build
WORKDIR /app

COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY eureka_server/build.gradle.kts eureka_server/build.gradle.kts

RUN gradle :eureka_server:dependencies --no-daemon || true

COPY eureka_server eureka_server

RUN gradle :eureka_server:bootJar --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/eureka_server/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]