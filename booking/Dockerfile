# Stage 1: Build
FROM openjdk:21-jdk-bullseye AS builder
WORKDIR /home/gradle/project
COPY . .
RUN chmod +x ./gradlew && \
    ./gradlew clean bootJar --no-daemon --stacktrace --info

# Stage 2: Runtime
FROM openjdk:21-jdk-bullseye
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/booking-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
