# Month 5 Backlog: Sales Opportunities And Pipeline Management

Month 5 should build on the Month 4 lead qualification work.

## Primary Goals

- Implement opportunity CRUD and pipeline stage management.
- Support opportunity value, expected close date, owner, company, and optional originating lead.
- Add stage transition history and validation rules.
- Allow qualified leads to seed opportunities without deleting or converting the original lead record.
- Build Angular opportunity list, detail, form, and pipeline views.

## Backend Work

- Refine existing opportunity schema with a new Flyway migration.
- Add DTOs, service layer, controller endpoints, and authorization rules.
- Add stage transition endpoint and history events.
- Keep manager/admin visibility broader than sales representative ownership.

## Frontend Work

- Replace opportunity placeholder screens with real Angular workflows.
- Add filters for stage, owner, company, and close date.
- Add a pipeline-oriented view suitable for Month 5 MVP review.

## Documentation Work

- Document opportunity APIs and stage rules.
- Update ERD and README.
- Add Month 5 review and release notes.
