# Month 8 Security Checklist

## Authentication

- JWT login remains the only credential exchange endpoint.
- Invalid bearer tokens clear the security context and do not authenticate a request.
- Passwords are handled through BCrypt in the Spring Security configuration.

## Authorization

- Admin-only administrative routes are protected by Spring Security.
- Lead visibility is scoped by admin/manager access or assigned/created ownership.
- Opportunity visibility is scoped by admin/manager access or owner assignment.
- Interaction visibility is scoped by admin/manager access, author, company owner, lead owner/creator, or opportunity owner.
- Dashboard metrics reuse existing scoped repositories and interaction service rules.

## Data Exposure

- API errors use structured responses and avoid stack traces in normal controller advice.
- DTOs are returned from business APIs rather than JPA entities.
- Client-supplied actor fields are not trusted for interaction creation or lead conversion.

## Follow-Up Items

- Add a real manager-team relationship if manager visibility must be narrower than all records.
- Add dependency scanning during April deployment work.
- Configure CI with Maven and ChromeHeadless so the test suite can run unattended.
