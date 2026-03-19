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
