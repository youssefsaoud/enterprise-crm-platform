# Release v0.5.0: Opportunity Pipeline

Version `v0.5.0` completes the Month 5 sales opportunity milestone.

## Highlights

- Opportunity data model with company, optional contact, owner, value, currency, expected close date, and description.
- Secured backend CRUD endpoints with service-layer authorization.
- Server-validated stage transitions and manager/admin owner reassignment.
- Pipeline summary grouped by stage and currency.
- Angular opportunity list, pipeline summary, create/edit form, and detail workflow.

## Operational Notes

- Apply Flyway migrations before using the Month 5 endpoints.
- Configure JWT authentication as described in the Month 2 setup.

## Verification Note

Automated tests and builds were intentionally skipped for this pass at the user's request.
