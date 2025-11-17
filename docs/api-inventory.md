# REST API Inventory

Business endpoints listed here are planned but not implemented in Month 1. Month 1 implements only the health endpoint.

## Implemented In Month 1

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/v1/health` | Public smoke endpoint for frontend-backend integration. |

## Planned Authentication

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/v1/auth/login` | Exchange credentials for a JWT. |
| POST | `/api/v1/auth/refresh` | Refresh an access token. |
| POST | `/api/v1/auth/logout` | End a session when refresh tokens are introduced. |

## Planned Users

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/v1/users` | List users with pagination. |
| GET | `/api/v1/users/{id}` | Read user details. |
| POST | `/api/v1/users` | Create a user. |
| PATCH | `/api/v1/users/{id}` | Update profile, status, or role. |

## Planned Companies

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/v1/companies` | List companies with pagination and sorting. |
| GET | `/api/v1/companies/{id}` | Read company details. |
| POST | `/api/v1/companies` | Create a company. |
| PATCH | `/api/v1/companies/{id}` | Update a company. |
| DELETE | `/api/v1/companies/{id}` | Soft-delete a company. |

## Planned Contacts

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/v1/contacts` | List contacts. |
| GET | `/api/v1/contacts/{id}` | Read contact details. |
| POST | `/api/v1/contacts` | Create a contact. |
| PATCH | `/api/v1/contacts/{id}` | Update a contact. |
| DELETE | `/api/v1/contacts/{id}` | Soft-delete a contact. |

## Planned Leads

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/v1/leads` | List leads by status, owner, or source. |
| GET | `/api/v1/leads/{id}` | Read lead details. |
| POST | `/api/v1/leads` | Create a lead. |
| PATCH | `/api/v1/leads/{id}` | Update a lead. |
| POST | `/api/v1/leads/{id}/assign` | Assign a lead to a representative. |
| POST | `/api/v1/leads/{id}/qualify` | Mark a lead as qualified. |
| POST | `/api/v1/leads/{id}/disqualify` | Mark a lead as disqualified. |
| POST | `/api/v1/leads/{id}/convert` | Convert a lead to an opportunity. |

## Planned Opportunities

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/v1/opportunities` | List opportunities. |
| GET | `/api/v1/opportunities/{id}` | Read opportunity details. |
| POST | `/api/v1/opportunities` | Create an opportunity. |
| PATCH | `/api/v1/opportunities/{id}` | Update an opportunity. |
| POST | `/api/v1/opportunities/{id}/stage` | Move an opportunity to a new stage. |
| DELETE | `/api/v1/opportunities/{id}` | Soft-delete an opportunity. |

## Planned Activities

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/v1/activities` | List activities by related record or author. |
| POST | `/api/v1/activities` | Log an activity. |
| GET | `/api/v1/activities/{id}` | Read activity details. |
| PATCH | `/api/v1/activities/{id}` | Update activity notes. |

## Planned Dashboard

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/v1/dashboard/summary` | Read CRM summary metrics. |
| GET | `/api/v1/dashboard/pipeline` | Read opportunity pipeline summary. |
