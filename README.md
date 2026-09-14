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

## Month 2 Authentication Setup

Month 2 adds Flyway schema migration `V2__month_02_core_schema.sql`, JPA mappings, Spring Security, JWT login, admin provisioning, and Angular protected navigation.

Required local authentication variables:

```text
CRM_JWT_SECRET=replace-with-at-least-32-random-characters
CRM_JWT_ISSUER=crm-api
CRM_JWT_EXPIRES_IN_SECONDS=3600
CRM_BOOTSTRAP_ADMIN_ENABLED=true
CRM_BOOTSTRAP_ADMIN_EMAIL=admin@example.com
CRM_BOOTSTRAP_ADMIN_PASSWORD=replace-with-local-password
```

Use `CRM_BOOTSTRAP_ADMIN_ENABLED=true` only in local development to create the first admin account. Disable it after provisioning.

Authentication endpoints:

```text
POST /api/v1/auth/login
GET  /api/v1/auth/me
POST /api/v1/admin/users   ADMIN only
```

The Angular app stores the access token in memory and clears it on logout or browser refresh.

## Month 3 Companies And Contacts

Month 3 adds secured company and contact management across the Spring Boot API and Angular app.

Company endpoints:

```text
GET    /api/v1/companies
POST   /api/v1/companies
GET    /api/v1/companies/{id}
PUT    /api/v1/companies/{id}
DELETE /api/v1/companies/{id}
GET    /api/v1/companies/{id}/contacts
```

Contact endpoints:

```text
GET    /api/v1/contacts
POST   /api/v1/contacts
GET    /api/v1/contacts/{id}
PUT    /api/v1/contacts/{id}
DELETE /api/v1/contacts/{id}
```

Angular routes:

```text
/companies
/companies/new
/companies/:id
/companies/:id/edit
/contacts
/contacts/new
/contacts/:id
/contacts/:id/edit
```

## Month 4 Lead Management

Month 4 adds lead capture, assignment, qualification status tracking, and lead history.

Lead endpoints:

```text
GET    /api/v1/leads
POST   /api/v1/leads
GET    /api/v1/leads/{id}
PUT    /api/v1/leads/{id}
DELETE /api/v1/leads/{id}
PATCH  /api/v1/leads/{id}/assignment
PATCH  /api/v1/leads/{id}/status
GET    /api/v1/leads/{id}/history
GET    /api/v1/users/sales-representatives
```

Angular routes:

```text
/leads
/leads/new
/leads/:id
/leads/:id/edit
```


## Month 5 Opportunity Pipeline

Month 5 adds secured sales opportunity management and pipeline summaries.

Opportunity endpoints:

```text
GET    /api/v1/opportunities
POST   /api/v1/opportunities
GET    /api/v1/opportunities/{id}
PUT    /api/v1/opportunities/{id}
DELETE /api/v1/opportunities/{id}
PATCH  /api/v1/opportunities/{id}/stage
PATCH  /api/v1/opportunities/{id}/owner
GET    /api/v1/opportunities/pipeline/summary
```

Angular routes:

```text
/opportunities
/opportunities/new
/opportunities/:id
/opportunities/:id/edit
```


## Month 6 Customer Interactions And Lead Conversion

Month 6 adds activity timelines and transactional qualified lead conversion.

Interaction endpoints:

```text
POST   /api/v1/interactions
GET    /api/v1/interactions
GET    /api/v1/interactions/{id}
PATCH  /api/v1/interactions/{id}
DELETE /api/v1/interactions/{id}
GET    /api/v1/leads/{id}/interactions
GET    /api/v1/companies/{id}/interactions
GET    /api/v1/contacts/{id}/interactions
GET    /api/v1/opportunities/{id}/interactions
```

Lead conversion endpoints:

```text
POST /api/v1/leads/{id}/convert
GET  /api/v1/leads/{id}/conversion
```


## Month 7 Dashboard And Integration

Month 7 adds dashboard endpoints.

