# Leads API

Month 4 adds secured lead management under `/api/v1/leads`.

## Access Rules

- Admin and Manager users can search all leads, edit any lead, assign leads, change statuses, and read history.
- Sales Representatives can create leads and read or edit leads they created or were assigned.
- Sales Representatives can change status only when assigned to the lead.
- Only Admin users can delete leads.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/v1/leads` | Create lead with status `NEW`. |
| `GET` | `/api/v1/leads` | Search visible leads. |
| `GET` | `/api/v1/leads/{id}` | Retrieve accessible lead. |
| `PUT` | `/api/v1/leads/{id}` | Update lead details. |
| `DELETE` | `/api/v1/leads/{id}` | Soft-delete lead, Admin only. |
| `PATCH` | `/api/v1/leads/{id}/assignment` | Assign, reassign, or unassign lead. |
| `PATCH` | `/api/v1/leads/{id}/status` | Change lead status. |
| `GET` | `/api/v1/leads/{id}/history` | Read lead event history. |
| `GET` | `/api/v1/users/sales-representatives` | List eligible assignees. |

## Search Parameters

`keyword`, `status`, `source`, `assigneeId`, `companyId`, `page`, `size`, and `sort` are supported. Sales representatives only receive leads they created or were assigned.

## Status Rules

Allowed transitions are:

```text
NEW -> CONTACTED
NEW -> UNQUALIFIED
CONTACTED -> QUALIFIED
CONTACTED -> UNQUALIFIED
```

`QUALIFIED` and `UNQUALIFIED` are terminal in Month 4. Invalid transitions return `409 Conflict`.

## Example Create Body

```json
{
  "firstName": "Jordan",
  "lastName": "Lee",
  "email": "jordan@example.com",
  "phone": "+1-555-0100",
  "companyName": "Northstar Labs",
  "source": "WEBSITE"
}
```

## Example Assignment Body

```json
{ "assigneeUserId": "0a8f1e6f-95c1-46a1-95a8-c0a93176839d" }
```

Use `null` for `assigneeUserId` to unassign a lead.
