# MVP Acceptance Tests

The April 2024 acceptance matrix records final MVP readiness. Results are project project documentation and should be updated with real deployment evidence during an actual production rollout.

## Role access matrix

| Area | Admin | Manager | Sales Representative | Expected result |
| --- | --- | --- | --- | --- |
| Login and `/auth/me` | Pass | Pass | Pass | Authenticated identity and role returned |
| Admin user creation | Pass | Blocked | Blocked | Backend enforces ADMIN only |
| Company CRUD | Pass | Pass | Read/create/update as allowed | Unauthorized actions rejected |
| Contact CRUD | Pass | Pass | Read/create/update as allowed | Company relationship preserved |
| Lead assignment | Pass | Pass | Limited | Assignment history recorded |
| Opportunity owner changes | Pass | Pass | Limited | Owner and stage changes persist |
| Dashboard | Pass | Pass | Pass | Role-scoped metrics returned |

## Security acceptance checks

| Check | Expected result |
| --- | --- |
| Missing token request | `401 Unauthorized` |
| Wrong role request | `403 Forbidden` |
| Logout in Angular | Token cleared and protected routes blocked |
| Session refresh | User must sign in again because token is stored in memory |
| CORS from unknown origin | Browser blocks request |
| Production secrets in source | None committed |
