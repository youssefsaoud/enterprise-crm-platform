# ADR 001: Use A Modular Monolith

## Status

Accepted for Month 1 foundation.

## Context

The CRM MVP needs companies, contacts, leads, opportunities, activities, users, authentication, and dashboard capabilities. These modules are related and will be developed by a small team. Operational simplicity matters more than independent deployment.

## Decision

Build one Spring Boot application organized as a modular monolith. Feature packages define boundaries, while one deployment unit keeps local development and testing simple.

## Consequences

### Benefits

- Simple local setup for a learning project.
- One database transaction boundary for related CRM workflows.
- Easier refactoring while the domain model is still evolving.
- Clear package boundaries without microservice overhead.

### Tradeoffs

- Modules are not independently deployable.
- Poor package discipline could still create coupling.
- Future extraction requires deliberate boundary cleanup.

## Security Notes

Authentication and role-based authorization are planned for Month 2. Month 1 must avoid implementing partial production security. Public health endpoints must not expose secrets, database details, user data, or environment values.
