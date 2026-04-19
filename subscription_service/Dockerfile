FROM gradle:9.3.1-jdk25 AS build
WORKDIR /app

COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY subscription_service/build.gradle.kts subscription_service/build.gradle.kts
COPY logging-common/build.gradle.kts logging-common/build.gradle.kts

RUN gradle :subscription_service:dependencies --no-daemon || true

COPY subscription_service subscription_service
COPY logging-common logging-common

RUN gradle :subscription_service:bootJar -x test --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/subscription_service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]