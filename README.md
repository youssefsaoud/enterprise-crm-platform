# Spring Boot Angular CRM

A Month 1 CRM foundation built as a modular monolith REST API with a separate Angular single-page application.

## Stack

- Java 17 target on Spring Boot 3.x
- Maven project structure
- PostgreSQL with Flyway migrations
- Angular 16 with TypeScript
- Angular Router and HttpClient

## Repository Layout

```text
backend/   Spring Boot API foundation
frontend/  Angular SPA foundation
docs/      Product, architecture, timeline, and release documentation
```

## Prerequisites

- JDK 17 or newer
- Maven 3.9 or newer
- PostgreSQL 14 or newer
- Node.js 16.14 through 20.x for Angular 16 compatibility
- npm or pnpm

## Environment

Copy `.env.example` into your local shell or environment manager and provide local values only. Do not commit secrets.

```text
CRM_DB_URL=jdbc:postgresql://localhost:5432/crm_dev
CRM_DB_USERNAME=crm_app
CRM_DB_PASSWORD=replace-with-local-password
CRM_CORS_ALLOWED_ORIGIN=http://localhost:4200
NG_APP_API_BASE_URL=http://localhost:8080/api/v1
```

## Database Setup

Create a local PostgreSQL database and application user before running the backend.

```sql
CREATE DATABASE crm_dev;
CREATE USER crm_app WITH PASSWORD 'replace-with-local-password';
GRANT ALL PRIVILEGES ON DATABASE crm_dev TO crm_app;
```

Flyway applies `backend/src/main/resources/db/migration/V1__initial_setup.sql` when the backend starts with a configured datasource.

## Backend Commands

```powershell
cd backend
mvn test
mvn spring-boot:run
```

The public health endpoint is:

```text
GET http://localhost:8080/api/v1/health
```

Expected response:

```json
{"status":"UP","service":"crm-api"}
```

## Frontend Commands

```powershell
cd frontend
pnpm install
pnpm run build
pnpm exec ng test --browsers=ChromeHeadless --watch=false
pnpm start
```

The Angular app runs at `http://localhost:4200` and proxies `/api` requests to `http://localhost:8080` during development.

## Month 1 Scope

Month 1 establishes requirements, architecture, backend foundation, frontend foundation, and integration documentation. It intentionally does not implement JWT authentication, role-based authorization, or business CRUD endpoints.
