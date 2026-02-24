FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy Gradle wrapper and build files first to maximize Docker cache usage
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Ensure wrapper script is executable on Linux images
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Copy source code and build fat JAR
COPY src src
RUN ./gradlew --no-daemon clean bootJar

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
