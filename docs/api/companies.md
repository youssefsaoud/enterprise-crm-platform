# Companies API

Month 3 adds secured company management endpoints under `/api/v1/companies`.

## Authorization

- `ADMIN`, `MANAGER`, and `SALES_REP` can list and view companies.
- `ADMIN` and `MANAGER` can create and update companies.
- Only `ADMIN` can delete companies.

All endpoints require a Bearer JWT except the existing health and login endpoints.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/v1/companies` | Create a company. |
| `GET` | `/api/v1/companies` | Search companies with pagination. |
| `GET` | `/api/v1/companies/{id}` | View company detail. |
| `PUT` | `/api/v1/companies/{id}` | Update company detail. |
| `DELETE` | `/api/v1/companies/{id}` | Delete a company if no contacts are attached. |
| `GET` | `/api/v1/companies/{id}/contacts` | List contacts for one company. |

## Search Query Parameters

| Name | Description |
| --- | --- |
| `search` | Case-insensitive partial match against company name. |
| `industry` | Case-insensitive exact industry filter. |
| `page` | Zero-based page number. |
| `size` | Page size. |
| `sort` | Spring Data sort expression such as `name,asc`. |

## Create Or Update Body

```json
{
  "name": "Northwind Traders",
  "industry": "Wholesale",
  "website": "https://northwind.example",
  "phone": "+1 555 0100",
  "email": "hello@northwind.example",
  "address": "12 Market Street"
}
```

`name` is required and trimmed. `email` must be valid when present. `website` must start with `http://` or `https://`.

## Deletion Policy

Company deletion is rejected with `409 Conflict` when active contacts are attached. Month 3 intentionally avoids silent cascade deletion from the API.
