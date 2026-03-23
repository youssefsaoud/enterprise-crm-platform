# Contacts API

Month 3 adds secured contact management endpoints under `/api/v1/contacts` and a company-scoped listing endpoint.

## Authorization

- `ADMIN`, `MANAGER`, and `SALES_REP` can list, view, create, and update contacts.
- `ADMIN` and `MANAGER` can delete contacts.
- `SALES_REP` cannot delete contacts.

Backend method security enforces these rules. Angular hides destructive actions where possible, but the API remains the source of truth.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/v1/contacts` | Create a contact linked to an existing company. |
| `GET` | `/api/v1/contacts` | Search contacts with optional company filtering. |
| `GET` | `/api/v1/contacts/{id}` | View contact detail. |
| `PUT` | `/api/v1/contacts/{id}` | Update contact and optionally move it to another company. |
| `DELETE` | `/api/v1/contacts/{id}` | Delete a contact when the role allows it. |
| `GET` | `/api/v1/companies/{companyId}/contacts` | List contacts for one company. |

## Search Query Parameters

| Name | Description |
| --- | --- |
| `search` | Case-insensitive partial match against first name, last name, or email. |
| `companyId` | Restrict results to one company UUID. |
| `page` | Zero-based page number. |
| `size` | Page size. |
| `sort` | Spring Data sort expression such as `lastName,asc`. |

## Create Or Update Body

```json
{
  "companyId": "0a8f1e6f-95c1-46a1-95a8-c0a93176839d",
  "firstName": "Amina",
  "lastName": "Bennis",
  "email": "amina@example.com",
  "phone": "+212 600 000000",
  "jobTitle": "Operations Manager"
}
```

`companyId`, `firstName`, and `lastName` are required. `email` must be valid when present. Duplicate names are allowed; Month 3 does not enforce global contact email uniqueness.

## Relationship Rules

A contact must reference an existing active company. The response includes `companyId` and `companyName` so Angular can navigate between contact and company views without exposing JPA entities directly.
