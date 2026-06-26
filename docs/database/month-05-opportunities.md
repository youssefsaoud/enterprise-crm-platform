# Month 5 Opportunity Database Notes

Month 5 refines the initial opportunity table created in Month 2 instead of editing older migrations.

## Migration

`backend/src/main/resources/db/migration/V5__month_05_opportunity_pipeline.sql`

## Table Changes

The migration adds opportunity fields needed by the sales pipeline:

- `contact_id` optional link to `contacts`
- `currency_code` with a three-letter uppercase check constraint
- `description` free text for deal context
- `deleted_at` for controlled soft deletion
- `version` for optimistic locking

The migration also replaces the older opportunity stage check constraint with the Month 5 stages:

```text
PROSPECTING, QUALIFICATION, PROPOSAL, NEGOTIATION, CLOSED_WON, CLOSED_LOST
```

## Integrity Rules

- Opportunity names are required and limited to 3 to 160 characters.
- Amounts use numeric precision and must be nonnegative.
- Company, owner, optional lead, and optional contact are protected by foreign keys.
- Contact-to-company consistency is enforced in the service layer because it depends on comparing the selected contact and company records.
- Soft-deleted opportunities are excluded from repository search and summary queries.

## Indexes

The migration adds indexes for common Month 5 access patterns:

- company lookup
- optional contact lookup
- owner plus stage filtering
- expected close date filtering
- case-insensitive name search

## MVP Scope Decision

Managers can currently access all opportunities. A future team hierarchy can narrow manager visibility without changing the public opportunity API shape.
