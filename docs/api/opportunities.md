# Month 5 Opportunity API

Month 5 adds secured opportunity management under the `/api/v1/opportunities` resource. All endpoints require a valid JWT.

## Endpoints

```text
GET    /api/v1/opportunities
POST   /api/v1/opportunities
GET    /api/v1/opportunities/{id}
PUT    /api/v1/opportunities/{id}
DELETE /api/v1/opportunities/{id}
PATCH  /api/v1/opportunities/{id}/stage
PATCH  /api/v1/opportunities/{id}/owner
GET    /api/v1/opportunities/pipeline/summary
```

## Search Parameters

```text
page, size, sort, search, stage, ownerId, companyId, closeDateFrom, closeDateTo
```

The backend caps page size and allows sorting only by supported opportunity fields.

## Create Or Update Payload

```json
{
  "name": "Annual support contract",
  "companyId": 42,
  "contactId": 105,
  "ownerUserId": 7,
  "amount": 15000.00,
  "currencyCode": "USD",
  "expectedCloseDate": "2024-01-31",
  "description": "Renewal and expansion opportunity"
}
```

Sales representatives always create opportunities owned by themselves. Managers and admins may provide an owner.

## Stage Update Payload

```json
{ "stage": "PROPOSAL" }
```

Supported stages are `PROSPECTING`, `QUALIFICATION`, `PROPOSAL`, `NEGOTIATION`, `CLOSED_WON`, and `CLOSED_LOST`.

## Owner Update Payload

```json
{ "ownerUserId": 7 }
```

Owner reassignment is limited to manager and admin users.

## Authorization Rules

- Admin users can view and manage all opportunities.
- Manager users can view and manage all opportunities in the MVP because no manager-team relationship exists yet.
- Sales representatives can view and update only their assigned opportunities.
- Deletes are soft deletes and are limited to manager/admin users.

## Pipeline Summary

`GET /api/v1/opportunities/pipeline/summary` returns grouped counts and values by stage and currency within the caller's authorized scope. Totals are grouped by currency so mixed currencies are never silently summed.
