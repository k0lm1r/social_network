FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY identity_service/build.gradle.kts identity_service/build.gradle.kts

RUN chmod +x ./gradlew

RUN ./gradlew :identity_service:dependencies --no-daemon || true

COPY identity_service identity_service

RUN ./gradlew :identity_service:bootJar --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/identity_service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]