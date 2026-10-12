# CRM MVP v1.0.0 Release Notes

Release: CRM MVP v1.0.0.

## Included MVP capabilities

- Spring Boot REST API with JWT authentication and role-based access control.
- PostgreSQL schema managed by Flyway migrations.
- Angular CRM frontend with protected routes and authenticated API access.
- Companies and contacts management.
- Lead management, assignment, status history, and conversion support.
- Opportunity pipeline management and dashboard metrics.
- Interaction logging and activity timelines.
- Deployment assets, CI workflow, operations runbook, UAT matrix, and handoff documentation.

## Known limitations

- No external email/calendar integration.
- No multi-tenant organization isolation.
- No mobile native application.
- Maven wrapper and full browser-test CI hardening remain post-MVP technical debt.

## Release decision

The MVP is ready for portfolio handoff once deployment secrets, backups, monitoring, and final smoke evidence are confirmed in the target environment.

