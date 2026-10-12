# Operations Runbook

This runbook covers routine operation of the CRM MVP.

## Daily checks

- API health endpoint returns `UP`.
- Angular frontend loads over HTTPS.
- Login succeeds for an admin test account.
- Recent logs have no repeated authentication, database, or migration failures.
- Latest PostgreSQL backup is present.

## Restart steps

1. Confirm whether the issue affects frontend, backend, or database.
2. Restart the affected service through the hosting provider.
3. Recheck `/api/v1/health`.
4. Verify login and one read-only CRM workflow.
5. Record the incident, impact window, and fix.

## Monitoring checklist

- Uptime probe for frontend root route.
- Uptime probe for backend health route.
- Alert on five consecutive failed probes.
- Alert on repeated HTTP 5xx responses.
- Alert on database storage approaching capacity.
- Log redaction checks for passwords, JWTs, and sensitive customer details.

## Production-safe logging

Backend logs should identify request failures without writing credentials, tokens, or full customer notes. Client-side errors should show friendly messages and avoid exposing stack traces to CRM users.

## Backup and restore rehearsal

Backup target:

- Daily PostgreSQL backup.
- Minimum retention: 14 days.
- Access restricted to project owner and deployment operator.

Restore rehearsal:

1. Create a nonproduction PostgreSQL database.
2. Restore the latest backup into the rehearsal database.
3. Start the API against the restored database with production bootstrap disabled.
4. Confirm companies, contacts, leads, opportunities, and interactions are readable.
5. Delete rehearsal credentials after the test.

## Incident response

Severity guide:

- Critical: login unavailable, database unavailable, or data loss suspected.
- High: core CRM workflow unavailable for a role.
- Medium: degraded dashboard or reporting behavior.
- Low: documentation, copy, or cosmetic defect.

For critical and high incidents, pause deployments, preserve logs, verify backup state, and communicate the workaround or recovery window before making code changes.

