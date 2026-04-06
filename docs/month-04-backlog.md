# Month 4 Backlog: Lead Management And Team Assignments

Month 4 should build on the authenticated CRM foundation and the Month 3 company/contact module.

## Primary Goals

- Implement lead CRUD using existing company and contact relationships.
- Add lead status workflow from `NEW` through qualification or disqualification.
- Assign leads to sales representatives and expose manager/admin reassignment flows.
- Add lead search, status filters, owner filters, and pagination.
- Build Angular lead list, detail, create, and edit workflows.
- Show related company and contact context on lead pages.

## Backend Work

- Refine the existing `Lead` JPA model as needed without editing applied migrations.
- Add request/response DTOs and a `LeadService` boundary.
- Add REST endpoints under `/api/v1/leads`.
- Enforce role rules in backend method security.
- Prevent invalid lead status transitions where the product policy requires it.
