FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy Maven descriptor first to maximize Docker cache usage
COPY pom.xml ./

# Copy source code and build fat JAR
COPY src src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
