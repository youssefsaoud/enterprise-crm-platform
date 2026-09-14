# Month 7 Dashboard API

Month 7 adds role-scoped CRM dashboard endpoints under `/api/v1/dashboard`. All endpoints require JWT authentication and reuse the existing backend authorization model.

## Endpoints

```text
GET /api/v1/dashboard/summary
GET /api/v1/dashboard/pipeline
GET /api/v1/dashboard/recent-activities?limit=10
```

## Metric Definitions

- `totalLeads`: count of non-deleted leads visible to the current user.
- `leadsByStatus`: visible lead counts grouped by lead status.
- `openOpportunities`: visible opportunities in `PROSPECTING`, `QUALIFICATION`, `PROPOSAL`, or `NEGOTIATION`.
- `openPipelineValue`: sum of open opportunity values grouped by currency code.
- `recentInteractionCount`: visible interactions that occurred in the last 7 days.

Pipeline totals are grouped by currency so mixed currencies are never silently summed.

## Role Scope

Admin and Manager users can see all dashboard records in the current MVP. Sales representatives see records they created, own, are assigned to, authored, or can access through opportunity ownership.

## Recent Activity

Recent activity returns the newest visible interactions first. The `limit` query parameter is clamped from 1 to 25.
