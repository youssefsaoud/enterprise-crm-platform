# Architecture

## Context

```text
+----------------------+       HTTPS/JSON        +--------------------------+       JDBC       +----------------+
| Angular SPA          |  <--------------------> | Spring Boot REST API     | <-------------> | PostgreSQL     |
| http://localhost:4200|                         | http://localhost:8080    |                 | crm_dev        |
+----------------------+                         +--------------------------+                 +----------------+
```

The CRM uses a separate Angular single-page application and a Spring Boot REST API. PostgreSQL is the source of truth for relational CRM data. Flyway owns database schema changes.

## Backend Architecture

The backend is a modular monolith. All features are deployed as one Spring Boot application, but code is grouped by business capability so the project can grow without collapsing into one large shared package.

Planned package boundaries under `com.example.crm`:

| Package | Responsibility |
| --- | --- |
| `auth` | Login, token issuing, and security rules planned for Month 2. |
| `users` | User accounts, role assignment, ownership metadata. |
| `companies` | Company records and company-level workflows. |
| `contacts` | Contacts linked to companies. |
| `leads` | Lead capture, assignment, qualification, and conversion planning. |
| `opportunities` | Pipeline opportunities and stage movement. |
| `activities` | Notes, calls, emails, meetings, and record history. |
| `dashboard` | Read-only summary views and metrics. |
| `common` | Shared web, configuration, validation, and error-handling support. |

Month 1 implements only technical foundation and a health endpoint. Business CRUD endpoints are intentionally deferred.

## Frontend Architecture

The Angular application is organized around routed feature areas:

- `core` for API clients and application-wide services.
- `layout` for shell, header, sidebar, and page structure.
- `features` for dashboard, companies, contacts, leads, opportunities, activities, auth, and status pages.
- `shared` for reusable UI conventions such as empty states and error messages.

The frontend reads the backend base URL from environment configuration and calls `/api/v1/health` to prove integration.

## API Conventions

- Version prefix: `/api/v1`.
- Payload format: JSON.
- Resource names: plural nouns.
- Status codes: standard HTTP semantics.
- Pagination: `page`, `size`, and `sort` query parameters for list endpoints.
- Sorting: `field,direction`, for example `createdAt,desc`.
- Validation errors: consistent JSON error response planned before CRUD implementation.

## Data And Audit Conventions

- Identifiers use UUIDs for public domain records.
- Tables include `created_at` and `updated_at` timestamps.
- User-owned records include `owner_id` or `assigned_to_id` where appropriate.
- Soft delete is planned for business records that should not disappear from history.
- Hard delete remains limited to administrative cleanup and non-business metadata.

## Security Considerations

- No real customer, employer, or client data is committed.
- JWT authentication and role-based authorization begin in Month 2.
- Month 1 health checks are public and intentionally contain no sensitive data.
- CORS is restricted to the local Angular development origin.
- Database credentials are provided by environment variables, not committed files.

## Tradeoffs

A modular monolith is simpler to build, test, and deploy than microservices at MVP scale. The architecture still keeps feature boundaries explicit, which makes future extraction possible if a real need appears.
