# ResiMed

ResiMed lets Care Homes and family Referents see the medical appointments of Residents,
received automatically from the hospitals and clinics (origins) that schedule them.

This repository contains the backend API: `appointments-app`.

## Tech stack

Java 25 · Spring Boot 4 · Spring Security (JWT resource server) · PostgreSQL · Flyway ·
JUnit 5 · Testcontainers · Maven

Architecture: hexagonal, package-by-feature. Decisions are recorded in [`docs/adr`](appointments-app/docs/adr).

## Prerequisites

- JDK 25
- Docker (running)

Maven is not required; the project includes the Maven wrapper (`./mvnw`).

## Run locally

```bash
cd appointments-app
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The `dev` profile uses Spring Boot's Docker Compose support: it starts PostgreSQL from
`docker-compose.yaml` (exposed on port `5433`), connects to it and runs the Flyway migrations.
No manual database setup is needed.

The API listens on `http://localhost:8080`. No endpoints are public yet, so any request
returns `401` with an RFC 7807 body:

```bash
curl -i http://localhost:8080/any/path
```

```json
{"detail":"Authentication required","status":401,"title":"Unauthorized"}
```

### Configuration

| Variable     | Required      | Description                                   |
|--------------|---------------|-----------------------------------------------|
| `JWT_SECRET` | outside `dev` | HS256 signing secret, at least 32 characters. |

The `dev` and `test` profiles provide a non-secret placeholder. Real secrets are never committed.

## Run the tests

```bash
cd appointments-app
./mvnw test
```

Integration tests start a PostgreSQL container with Testcontainers, so Docker must be running.

Run a single test class:

```bash
./mvnw test -Dtest=SecurityConfigTest
```