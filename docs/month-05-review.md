# Month 5 Review: Sales Opportunities And Pipeline Management

Month 5 delivers the sales opportunity module on top of the authenticated CRM foundation.

## Completed Scope

- Added opportunity pipeline schema refinements with constraints, indexes, and soft deletion.
- Added `Opportunity` JPA mapping, `OpportunityStage`, repository filters, and summary projection.
- Added DTOs, service-layer validation, object-level authorization, CRUD, stage changes, owner reassignment, and pipeline summary endpoints.
- Added Angular opportunity models, API service, list/pipeline page, create/edit form, and detail workflow.
- Updated documentation for API behavior, database design, and release scope.

## Authorization Notes

Admin and manager users can manage all opportunities in the MVP. Sales representatives can work only with opportunities assigned to themselves. Manager team scoping remains a future enhancement because the current data model does not include manager-team membership.

## Testing Note

Per the current project workflow request, automated tests, builds, and smoke checks were not run during this Month 5 implementation pass.

## Timeline Note

