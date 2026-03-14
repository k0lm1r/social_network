FROM eclipse-temurin:25-jdk

WORKDIR /app
COPY build.gradle.kts .
COPY gradle ./gradle
RUN cmod -x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

COPY src ./src
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]