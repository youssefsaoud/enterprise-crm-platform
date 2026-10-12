# Spring Boot Angular CRM

A full-stack CRM portfolio project built with Spring Boot, Angular, and PostgreSQL. The application provides secured customer relationship management workflows for companies, contacts, leads, opportunities, interactions, and dashboard reporting.

## Tech Stack

- Java 17, Spring Boot 3, Spring Security, Spring Data JPA
- PostgreSQL with Flyway migrations
- Angular 16, TypeScript, Angular Router, HttpClient
- JWT authentication and role-based authorization
- Docker, Docker Compose, nginx, GitHub Actions CI

## Features

- JWT login, current-user lookup, logout flow, and protected Angular routes
- Admin user provisioning and role-based backend access control
- Company and contact CRUD with search, filtering, pagination, and relationships
- Lead capture, assignment, status tracking, history, and conversion workflow
- Opportunity pipeline management with stage and owner updates
- Interaction logging for calls, emails, meetings, and notes
- Dashboard KPIs, pipeline summaries, lead status metrics, and recent activity
- Production-oriented Docker assets, deployment guide, and operations runbook

## Repository Structure

```text
backend/                Spring Boot REST API
frontend/               Angular single-page application
docs/api/               Endpoint documentation
docs/database/          Schema and ERD documentation
docs/security/          Security and permission notes
docs/deployment.md      Deployment guide
docs/operations-runbook.md
.github/workflows/ci.yml
```

## Prerequisites

- JDK 17+
- Maven 3.9+
- PostgreSQL 14+
- Node.js 16.14 through 20.x
- pnpm
- Docker, optional for containerized setup

## Environment

Copy `.env.example` into your local environment manager and provide real local values. Do not commit secrets.

```text
CRM_DB_URL=jdbc:postgresql://localhost:5432/crm_dev
CRM_DB_USERNAME=crm_app
CRM_DB_PASSWORD=replace-with-local-password
CRM_CORS_ALLOWED_ORIGIN=http://localhost:4200
CRM_JWT_SECRET=replace-with-at-least-32-random-characters
CRM_BOOTSTRAP_ADMIN_ENABLED=true
CRM_BOOTSTRAP_ADMIN_EMAIL=admin@example.com
CRM_BOOTSTRAP_ADMIN_PASSWORD=replace-with-local-password
```

## Backend Setup

Create the PostgreSQL database and application user:

```sql
CREATE DATABASE crm_dev;
CREATE USER crm_app WITH PASSWORD 'replace-with-local-password';
GRANT ALL PRIVILEGES ON DATABASE crm_dev TO crm_app;
```

Run the API:

```powershell
cd backend
mvn spring-boot:run
```

Flyway applies migrations from `backend/src/main/resources/db/migration` on startup.

## Frontend Setup

```powershell
cd frontend
pnpm install
pnpm start
```

The Angular app runs at `http://localhost:4200` and proxies `/api` requests to the backend during local development.

## Docker Setup

```powershell
docker compose build
docker compose up -d
```

The compose stack starts PostgreSQL, the Spring Boot API, and the Angular/nginx frontend.

## API Overview

Base path: `/api/v1`

| Area | Endpoints |
| --- | --- |
| Auth | `POST /auth/login`, `GET /auth/me` |
| Admin | `POST /admin/users` |
| Users | `GET /users/sales-representatives` |
| Companies | `/companies`, `/companies/{id}`, `/companies/{id}/contacts` |
| Contacts | `/contacts`, `/contacts/{id}` |
| Leads | `/leads`, `/leads/{id}`, `/leads/{id}/assignment`, `/leads/{id}/status`, `/leads/{id}/history`, `/leads/{id}/convert` |
| Opportunities | `/opportunities`, `/opportunities/{id}`, `/opportunities/{id}/stage`, `/opportunities/{id}/owner`, `/opportunities/pipeline/summary` |
| Interactions | `/interactions`, `/leads/{id}/interactions`, `/companies/{id}/interactions`, `/contacts/{id}/interactions`, `/opportunities/{id}/interactions` |
| Dashboard | `/dashboard/summary`, `/dashboard/pipeline`, `/dashboard/recent-activities` |
| Health | `/health` |

Detailed endpoint notes are in `docs/api/`.

## Documentation

- `docs/architecture.md`
- `docs/deployment.md`
- `docs/operations-runbook.md`
- `docs/troubleshooting.md`
- `docs/user-guide.md`
- `docs/database/schema.md`
- `docs/database/erd.md`
- `docs/security/role-permission-matrix.md`
- `docs/security/token-storage.md`
- `docs/release-notes-v1.0.0.md`

## Release

`v1.0.0` represents the completed CRM MVP portfolio release.
