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
