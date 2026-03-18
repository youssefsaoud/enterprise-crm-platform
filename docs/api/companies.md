# Companies API

Month 3 adds secured company management endpoints under `/api/v1/companies`.

## Authorization

- `ADMIN`, `MANAGER`, and `SALES_REP` can list and view companies.
- `ADMIN` and `MANAGER` can create and update companies.
- Only `ADMIN` can delete companies.
