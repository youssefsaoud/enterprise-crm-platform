# Month 6 Interaction API

Month 6 adds customer activity tracking and lead conversion APIs. All routes require JWT authentication.

## Interaction Endpoints

```text
POST   /api/v1/interactions
GET    /api/v1/interactions
GET    /api/v1/interactions/{id}
PATCH  /api/v1/interactions/{id}
DELETE /api/v1/interactions/{id}
GET    /api/v1/leads/{id}/interactions
GET    /api/v1/companies/{id}/interactions
GET    /api/v1/contacts/{id}/interactions
GET    /api/v1/opportunities/{id}/interactions
```

## Interaction Payload

```json
{
  "type": "CALL",
  "subject": "Discovery call",
  "description": "Reviewed budget and next steps.",
  "occurredAt": "2024-01-12T10:30:00+01:00",
  "followUpAt": "2024-01-16T09:00:00+01:00",
  "leadId": "..."
}
```

An interaction must link to at least one company, contact, lead, or opportunity. Multiple links are allowed only when they resolve to the same customer context.

## Lead Conversion Endpoints

```text
POST /api/v1/leads/{id}/convert
GET  /api/v1/leads/{id}/conversion
```

Only `QUALIFIED` leads can be converted. Conversion is transactional and either reuses or creates a company/contact, optionally creates an opportunity, marks the lead `CONVERTED`, and stores one immutable conversion result.

## Retention Policy

Interactions use soft deletion and are not cascaded away during lead conversion. The source lead remains available for history, and the conversion result links the lead to the resulting company, contact, and optional opportunity.
