FROM gradle:9.3.1-jdk25 AS build
WORKDIR /app

COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY feed_service/build.gradle.kts feed_service/build.gradle.kts
COPY common/logging-common/build.gradle.kts common/logging-common/build.gradle.kts
COPY common/auth-contract/build.gradle.kts common/auth-contract/build.gradle.kts
COPY common/auth-spring-security/build.gradle.kts common/auth-spring-security/build.gradle.kts

RUN gradle :feed_service:dependencies --no-daemon || true

COPY feed_service feed_service
COPY common/logging-common common/logging-common
COPY common/auth-contract common/auth-contract
COPY common/auth-spring-security common/auth-spring-security

RUN gradle :feed_service:bootJar -x test --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/feed_service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
