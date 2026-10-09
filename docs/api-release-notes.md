# API Release Notes

The v1.0.0 API includes secured CRM endpoints for authentication, users, companies, contacts, leads, lead conversion, opportunities, interactions, and dashboard summaries.

## Stability notes

- Public API base path is `/api/v1`.
- Authentication uses JWT bearer tokens.
- Authorization is enforced in backend controllers and services.
- Flyway migrations define the database schema from initial setup through interactions and conversions.

## Operational notes

- Use `/api/v1/health` for health checks.
- Use production profile and externalized secrets in deployed environments.
- Review migration scripts before each release tag deployment.
