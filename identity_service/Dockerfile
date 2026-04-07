FROM gradle:9.3.1-jdk25 AS build
WORKDIR /app

COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY identity_service/build.gradle.kts identity_service/build.gradle.kts
COPY proto/build.gradle.kts proto/build.gradle.kts

RUN gradle :identity_service:dependencies --no-daemon || true

COPY identity_service identity_service
COPY proto proto

RUN gradle :identity_service:bootJar --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/identity_service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]