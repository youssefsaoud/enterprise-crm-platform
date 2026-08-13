# Release v0.6.0: Interactions And Lead Conversion

Version `v0.6.0` completes the Month 6 customer interaction and lead conversion milestone.

## Highlights

- Activity logging for calls, emails, meetings, and notes.
- Timelines on company, contact, lead, and opportunity records.
- Transactional qualified lead conversion into company/contact records and optional opportunities.
- Conversion metadata with duplicate-conversion protection.
- Angular timeline and conversion workflows.

## Operational Notes

- Apply Flyway migration `V6__month_06_interactions_and_conversion.sql` before using the Month 6 endpoints.
- Interactions are soft-deleted and retained through lead conversion.

## Verification Note

Automated tests and builds were intentionally skipped for this pass at the user's request.
