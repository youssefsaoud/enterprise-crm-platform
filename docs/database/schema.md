# Month 2 Database Schema

The Month 2 schema uses six core CRM tables: `app_users`, `companies`, `contacts`, `leads`, `opportunities`, and `interactions`. The schema is managed by Flyway and extends the Month 1 technical baseline without editing existing migrations.

## Decisions

- `app_users` is used instead of `users` to avoid collisions with reserved words and database-specific conventions.
- One role per user is sufficient for the MVP: `ADMIN`, `MANAGER`, or `SALES_REP`.
- `interactions` use explicit nullable foreign keys to CRM records plus a check constraint requiring exactly one related parent.
- Monetary values use `NUMERIC(14,2)` with a nonnegative check constraint.
- User email uniqueness is enforced through `normalized_email`.
- Soft delete is available on business records that should remain historically traceable.

## Relationship Summary

- A company has many contacts.
- A company has many opportunities.
- A lead may reference a company, a contact, and an assigned user.
- An opportunity belongs to one company and may be linked to one converted lead.
- An interaction belongs to an author and exactly one company, contact, lead, or opportunity.
