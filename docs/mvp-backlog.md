# MVP Backlog

## MVP

| Priority | Feature | Description | Acceptance Criteria |
| --- | --- | --- | --- |
| P0 | Project foundation | Create Spring Boot backend and Angular frontend in a monorepo. | Backend and frontend can be started from documented commands. |
| P0 | Health check | Expose a public backend health endpoint. | `GET /api/v1/health` returns `200` with service status JSON. |
| P0 | App shell | Provide Angular layout with header, navigation, and routed pages. | Users can navigate placeholder pages for all planned modules. |
| P0 | Companies | Manage basic company records. | Users can create, list, view, and edit company details. |
| P0 | Contacts | Manage contacts under companies. | Contacts are linked to companies and validated. |
| P0 | Leads | Track lead status and ownership. | Leads can be assigned, qualified, and disqualified. |
| P0 | Opportunities | Track sales pipeline opportunities. | Opportunities have owner, company, stage, value, and close date. |
| P1 | Activities | Log notes, calls, emails, and meetings. | Activities appear in chronological history for related records. |
| P1 | Dashboard | Show high-level CRM summaries. | Dashboard displays useful empty states and later module counts. |
| P1 | Authentication | Add JWT login and protected routes. | Users can authenticate and access role-appropriate pages. |

## Nice To Have

| Feature | Reason For Deferral |
| --- | --- |
| Advanced dashboard charts | Needs stable business data first. |
| Email integration | Requires external provider and security review. |
| Import/export | Useful after core entities are stable. |
| Notifications | Requires activity and assignment rules. |
| Custom fields | Adds complexity before MVP workflows are proven. |

## Out Of Scope For Month 1

- Production-ready JWT authentication.
- Full role-based authorization.
- Customer, contact, lead, and opportunity CRUD implementation.
- Production dashboard metrics.
- Deployment and CI/CD automation.
- Real customer or employer data.

## September Backlog Seeds

- Implement initial domain tables with Flyway.
- Add user and role persistence.
- Implement JWT authentication.
- Protect backend API routes and Angular routes.
- Begin company and contact CRUD.
