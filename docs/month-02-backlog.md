# Month 2 Backlog

Month 2 begins the database schema, authentication, and role-based access control implementation. These items are intentionally not implemented in Month 1.

## Priority 0

| Item | Outcome |
| --- | --- |
| Domain schema migrations | Flyway migrations create users, companies, contacts, leads, opportunities, activities, and supporting lookup data. |
| User persistence | Users can be stored with role, active flag, and audit fields. |
| JWT authentication | Login endpoint issues signed access tokens. |
| Password hashing | User passwords are stored with a secure one-way hash. |
| Backend security filter | Protected API routes require authentication. |
| Angular auth shell | Frontend can store login state and protect placeholder routes. |

## Priority 1

| Item | Outcome |
| --- | --- |
| Role-based authorization | Admin, manager, and sales representative permissions map to backend policies. |
| Seed data | Fictional demo users and CRM records support local development. |
| Company CRUD | First business module supports list, create, read, and update workflows. |
| Contact CRUD | Contacts can be linked to companies. |
| Error response contract | Validation and authorization errors use consistent JSON responses. |

## Out Of Scope For Month 2

- External email or calendar integrations.
- Production deployment automation.
- Advanced dashboard analytics.
- Custom fields.
