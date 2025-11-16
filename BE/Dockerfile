FROM eclipse-temurin:17-jdk
LABEL authors="kimberleymyburgh"

FROM gradle:8.9-jdk17 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]