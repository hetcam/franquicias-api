# franquicias-api

RESTful API for Franquicias, built with **Java 17**, **Spring Boot**, and **Gradle**.

## Requirements

- Java 17 or later
- (Optional) Gradle 8.x — if not using the wrapper

## Build and run

```bash
# Build
./gradlew build

# Run the application
./gradlew bootRun
```

On Windows:

```cmd
gradlew.bat build
gradlew.bat bootRun
```

The API will be available at `http://localhost:8080`.

## API

| Method | Path        | Description    |
|--------|-------------|----------------|
| GET    | /api/health | Health check   |

## Project structure

- `src/main/java/com/franquicias/` — application and REST controllers
- `src/main/resources/` — `application.properties` and static resources
- `build.gradle` — Gradle build and dependencies (Spring Boot 3.2, Java 17)
