Employee Management System (Microservices)

Overview

- Architecture: Spring Boot microservices with Spring Cloud Config, Eureka Discovery, and Spring Cloud Gateway.
- Services:
  - auth-service: Registration and login; issues JWTs (no persistence of tokens).
  - employee-service: CRUD for Employees and Departments; publishes Kafka events.
  - api-gateway: Routes /api/* to services and enforces JWT on employee-service routes.
  - discovery-service: Eureka server for service discovery.
  - config-server: Spring Cloud Config Server serving configuration from the local config-repo folder.
- Infrastructure: Postgres (two DBs), Kafka + Zookeeper via docker-compose.

Prerequisites

- Java 17. Use your script to switch: `C:\Windows\JDKScripts\Set-Java17.ps1`.
- Maven 3.9+ (or use the Maven Wrapper `mvnw`).
- Docker + Docker Compose for databases and Kafka.

Configuration

- Config repository: `config-repo` in the root. The config-server reads it from a local file URI.
- Key config files:
  - `config-repo/application.yml`: Common settings (JPA, Eureka, actuator).
  - `config-repo/employee-service.yml`: Postgres + Kafka settings for employee-service.
  - `config-repo/auth-service.yml`: Postgres and JWT secret for auth-service.
  - `config-repo/api-gateway.yml`: Gateway and JWT secret for the filter.

Run Infrastructure

- Start Postgres and Kafka:
  - `docker-compose up -d`
  - Postgres ports: employee-db on 5432; auth-db on 5433 (internal DB names differ from compose; configs use localhost ports).
  - Kafka broker on 9092.

Run Config Server

- From `config-server`:
  - Windows PowerShell: `C:\Windows\JDKScripts\Set-Java17.ps1`
  - `mvn -DskipTests spring-boot:run`
- It will serve configs from `file:///${user.dir}/../config-repo` at `http://localhost:8888`.

Run Discovery (Eureka)

- From `discovery-service`:
  - `mvn -DskipTests spring-boot:run`
- Dashboard at `http://localhost:8761`.

Run API Gateway

- From `api-gateway`:
  - `mvn -DskipTests spring-boot:run`
- Listens on port from config-repo (default 9090). Routes:
  - `/api/auth/**` → `auth-service` (no JWT required)
  - `/api/employees/**`, `/api/departments/**` → `employee-service` (JWT required)

Run Services

- auth-service (port 8081):
  - Ensure `config-server` and `auth-db` are running.
  - `mvn -DskipTests spring-boot:run`

- employee-service (port 8082):
  - Ensure `config-server`, `employee-db`, Kafka, and Eureka are running (Eureka optional if `fail-fast` disabled).
  - `mvn -DskipTests spring-boot:run`

Testing

- Unit and integration tests:
  - employee-service uses H2 for JPA tests and Mockito for service tests.
  - auth-service has repository, service, and controller tests using a `test` profile.
- To run tests for each service:
  - `mvn test`
- employee-service test profile defined at `employee-service/src/test/resources/application-test.yml` disables Flyway, Eureka, and Config Client and configures in-memory H2.

Security & JWT

- The API Gateway parses the `Authorization: Bearer <token>` header, validates with the shared `jwt.secret` from config, and forwards identity to downstream services via headers:
  - `X-User-Id` (JWT subject)
  - `X-User-Role` (claim `role`)
- employee-service enforces simple header-based rules (ADMIN required for mutating operations, EMPLOYEE may only see own record via `employeeId`).

Kafka Events (employee-service)

- Topic names configured in code: `employee-events`, `department-events`.
- Producer publishes `EmployeeEvent` for create/update/delete with `EventType` and core employee attributes.

OpenAPI / Swagger

- employee-service: OpenAPI configured via `OpenApiConfig` with servers (local and via gateway) and Bearer security scheme.
- auth-service: OpenAPI enabled with simple tag and endpoint docs.
- Swagger UI (when enabled by the `springdoc-openapi-starter`):
  - `http://localhost:8082/swagger-ui.html` (employee-service)
  - `http://localhost:8081/swagger-ui.html` (auth-service)

Development Notes & Fixes Applied

- Aligned package paths to match Maven conventions:
  - employee-service → `com.employeemgmt.employeeservice` (moved all classes and tests).
  - auth-service → `com.employeemgmt.authservice` (fixed Application class and exception + tests packages).
  - api-gateway → `com.employeemgmt.apigateway` (moved app, config, filter, and tests).
- Improved OpenAPI annotations for accurate response codes and descriptions in controllers.
- employee-service events now use `EventType` enum instead of raw strings for type safety.
- Tests: employee-service context test now uses `@ActiveProfiles("test")`.

Common Pitfalls

- Ensure Java 17 before building. In a new shell run the script again.
- Config server must be reachable at `http://localhost:8888` for runtime external configs; otherwise, add local `application.yml` with necessary datasource/Kafka properties for dev.
- Kafka must be up for publishing events; otherwise, producer send calls will log errors.

Build Commands

- From each service directory:
  - Windows PowerShell: `C:\Windows\JDKScripts\Set-Java17.ps1`
  - `mvn -DskipTests package`

Endpoints Overview (employee-service)

- `POST /employees` (ADMIN) → create employee (201)
- `PUT /employees/{id}` (ADMIN) → update employee (200)
- `DELETE /employees/{id}` (ADMIN) → delete employee (204)
- `GET /employees/{id}` (ADMIN/MANAGER; EMPLOYEE self) → get by id (200)
- `GET /employees` (ADMIN/MANAGER) → list all (200)
- `GET /employees/department/{departmentId}` (ADMIN/MANAGER) → list by department (200)
- `GET /employees/status/{status}` (ADMIN/MANAGER) → list by status (200)
- `GET /employees/search?name=...` (ADMIN/MANAGER) → search by name (200)

Auth Endpoints (auth-service)

- `POST /auth/login` → returns JWT on success (200)
- `POST /auth/register` → create user (201)
- `GET /auth/health` → health check (200)

