# Month 6 Database Notes

Month 6 extends the CRM schema with customer interaction history and lead conversion metadata.

## Migration

`backend/src/main/resources/db/migration/V6__month_06_interactions_and_conversion.sql`

## Interaction Changes

The existing `interactions` table is expanded with:

- `description` for bounded activity notes.
- `follow_up_at` for manual follow-up tracking.
- `updated_at` and `deleted_at` for audit and soft deletion.
- A relaxed parent constraint requiring at least one linked CRM record instead of exactly one.
- Indexes for linked records and `occurred_at` timeline sorting.

## Lead Conversion

The `lead_conversions` table stores one conversion result per lead. It links the source lead to the resulting company, contact, optional opportunity, conversion actor, timestamp, and notes.

## Status Update

The lead status check constraint now includes `CONVERTED`. Conversion services set this status only after all related records are created or selected successfully in one transaction.
