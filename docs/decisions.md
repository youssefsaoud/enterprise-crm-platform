# Project Decisions

## Initial Assumptions

- Use a monorepo with `backend/` and `frontend/`.
- Build the backend as a Spring Boot modular monolith.
- Build the frontend as a separate Angular SPA.
- Use PostgreSQL for relational data.
- Manage database changes through Flyway migrations.
- Use `/api/v1` as the public REST API prefix.
- Use Java 21 for new backend implementation work.
- Keep JWT authentication and role-based authorization for Month 2.
- Use fictional demo data only.

## Open Questions

- Should lead assignment support teams, territories, or round-robin rules?
- Should activities attach to one generic record reference or typed foreign keys?
- Should opportunity stages be configurable or fixed at launch?
- Should soft delete apply to every business table or only high-risk records?

## Deferred Decisions

- Exact JWT token expiry policy.
- Full audit log design.
- Production deployment target.
- Email or calendar integration.
