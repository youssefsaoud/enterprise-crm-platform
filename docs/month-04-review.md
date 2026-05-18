# Month 4 Review: Lead Management And Team Assignments

Month 4 adds a secured lead module spanning persistence, REST APIs, Angular screens, assignment workflow, qualification status changes, and audit history.

## Completed Scope

- Added Flyway migration `V4__month_04_lead_management.sql`.
- Added lead status, source, and event types.
- Added `Lead` and `LeadEvent` persistence models with optimistic versioning.
- Added lead CRUD, filtered search, soft deletion, assignment, status transition, and history endpoints.
- Added eligible sales representative lookup for assignment controls.
- Added Angular lead list, create/edit form, detail, assignment, status, and history screens.
- Updated API, database, README, and release documentation.

## Authorization Summary

- Admin and Manager users can search all leads and assign/reassign leads.
- Sales representatives see leads they created or were assigned.
- Sales representatives can change status only on assigned leads.
- Only Admin users can delete leads.

## Verification Note

Per the current workflow instruction, automated tests, builds, and smoke checks were not run during Month 4 implementation.

## Release

Recommended milestone tag: `v0.4.0`.
