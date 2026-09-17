# Release v0.7.0: Dashboard And Integration

Version `v0.7.0` completes the Month 7 dashboard and full-stack integration milestone.

## Highlights

- Role-scoped dashboard summary endpoint.
- Pipeline aggregation endpoint grouped by stage and currency.
- Recent activity endpoint with bounded limits.
- Angular dashboard with KPI cards, lead status counts, pipeline visualization, and recent activity links.
- Activities page backed by the interaction API.

## Operational Notes

- Dashboard endpoints require the same JWT setup introduced in Month 2.
- Pipeline values are grouped by currency code.
- Recent interaction count covers the last 7 days.

## Verification Note

Automated tests and builds were intentionally skipped for this pass at the user's request.
