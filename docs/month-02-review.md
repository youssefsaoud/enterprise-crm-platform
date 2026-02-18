# Month 2 Review

## Implemented

- PostgreSQL schema migration for users, companies, contacts, leads, opportunities, and interactions.
- Database documentation for relationships, constraints, and ERD decisions.
- JPA entities, enums, and repositories for the six core CRM records.
- Spring Security configuration with stateless JWT authentication.
- BCrypt password hashing and development-only first admin bootstrap.
- Public login endpoint and authenticated `/api/v1/auth/me` endpoint.
- Admin-only user provisioning endpoint.
- Representative backend RBAC endpoints.
- Angular login form, AuthService, bearer interceptor, route guard, identity display, and logout.
- Security documentation for role permissions and in-memory token storage.

## Not Run By Request

Tests, builds, smoke checks, and database migration execution were intentionally not run during this implementation pass.

## Deferred

- Full CRM CRUD workflows.
- Lead assignment rules and team scoping beyond role checks.
- Password reset and refresh tokens.
- Production deployment hardening.
