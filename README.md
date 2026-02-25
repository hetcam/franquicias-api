# franquicias-api

RESTful API for franquicias, sucursales, and productos built with **Java 17**, **Spring Boot**, and **Maven**.

## What this project does

The API allows you to:

- Create franquicias
- Create sucursales for a franquicia
- Create, update, and delete productos in a sucursal
- Query the product with the maximum stock per sucursal in a franquicia
- Check service health

## Requirements

- Java 17+
- Maven 3.9+

## Run locally

### Build

Linux/macOS:

```bash
mvn clean verify
```

Windows:

```cmd
mvn clean verify
```

### Start the API

Linux/macOS:

```bash
mvn spring-boot:run
```

Windows:

```cmd
mvn spring-boot:run
```

By default, the API runs at:

- `http://localhost:8080`

## Run with Docker

### Option 1: Docker Compose (recommended)

Build and start:

```bash
docker compose up --build
```

Run in background:

```bash
docker compose up --build -d
```

Stop and remove container:

```bash
docker compose down
```

### Option 2: Docker only

Build image:

```bash
docker build -t franquicias-api:latest .
```

Run container:

```bash
docker run --rm -p 8080:8080 --name franquicias-api franquicias-api:latest
```

### Notes

- The image uses a multi-stage build and compiles the project with Maven inside Docker.
- The app remains available at `http://localhost:8080`.

## How to use the API (quick flow)

### 1) Health check

```bash
curl http://localhost:8080/api/health
```

### 2) Create a franquicia

```bash
curl -X POST http://localhost:8080/api/franquicias \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Franquicia Centro\"}"
```

### 3) Create a sucursal in the franquicia

```bash
curl -X POST http://localhost:8080/api/franquicias/1/sucursales \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Sucursal Norte\"}"
```

### 4) Create a producto in the sucursal

```bash
curl -X POST http://localhost:8080/api/sucursales/1/productos \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Producto A\",\"description\":\"Descripcion\",\"stock\":10}"
```

### 5) Get max-stock producto per sucursal

```bash
curl http://localhost:8080/api/franquicias/1/productos-max-stock
```

## OpenAPI / Swagger documentation

The project generates OpenAPI documentation automatically from the controllers and annotations.

### Endpoints

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Recommended way to explore endpoints

1. Start the app with `mvn spring-boot:run`.
2. Open Swagger UI in your browser.
3. Expand a tag (`Franquicias`, `Productos`, `Health`).
4. Click an endpoint and select **Try it out**.
5. Fill request data and execute the request.
6. Review the response, status code, and generated schema.

### Use OpenAPI spec in tools

You can use the JSON spec (`/v3/api-docs`) to:

- Import the API into Postman/Insomnia
- Generate clients (OpenAPI Generator)
- Share an always up-to-date contract for frontend/backend integration

## Main API endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/health` | Health check |
| POST | `/api/franquicias` | Create franquicia |
| POST | `/api/franquicias/{franquiciaId}/sucursales` | Create sucursal in franquicia |
| PATCH | `/api/franquicias/{franquiciaId}/name` | Update franquicia name |
| PATCH | `/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/name` | Update sucursal name |
| GET | `/api/franquicias/{franquiciaId}/productos-max-stock` | Get max-stock producto per sucursal |
| POST | `/api/sucursales/{sucursalId}/productos` | Create producto in sucursal |
| DELETE | `/api/sucursales/{sucursalId}/productos/{productoId}` | Delete producto from sucursal |
| PATCH | `/api/sucursales/{sucursalId}/productos/{productoId}/stock` | Update producto stock |
| PATCH | `/api/sucursales/{sucursalId}/productos/{productoId}/name` | Update producto name |

## Project structure

- `src/main/java/com/franquicias/` - application, controllers, services, entities, and DTOs
- `src/main/resources/` - `application.properties`
- `pom.xml` - dependencies and build configuration
