# MVP Release Checklist

## Infrastructure

- [ ] Frontend is accessible over HTTPS.
- [ ] API is accessible over HTTPS.
- [ ] PostgreSQL is isolated from public traffic.
- [ ] CORS allows only the production frontend origin.
- [ ] JWT secret and database credentials are injected as secrets.

## Application

- [ ] Health endpoint returns `UP`.
- [ ] Admin login succeeds.
- [ ] Role-specific access policies are enforced.
- [ ] Core company, contact, lead, opportunity, interaction, and dashboard flows pass.

## Operations

- [ ] Latest backup exists.
- [ ] Restore rehearsal is documented.
- [ ] Alerts are routed.
- [ ] Rollback path is documented.
