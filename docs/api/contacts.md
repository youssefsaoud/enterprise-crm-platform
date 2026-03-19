# Contacts API

Month 3 adds secured contact management endpoints under `/api/v1/contacts` and a company-scoped listing endpoint.

## Authorization

- `ADMIN`, `MANAGER`, and `SALES_REP` can list, view, create, and update contacts.
- `ADMIN` and `MANAGER` can delete contacts.
- `SALES_REP` cannot delete contacts.
