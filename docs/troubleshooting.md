# Troubleshooting

## API will not start

- Confirm `CRM_DB_URL`, `CRM_DB_USERNAME`, and `CRM_DB_PASSWORD` are set.
- Confirm PostgreSQL is reachable from the API service.
- Check Flyway logs for migration failures.

## Login fails

- Confirm the admin user was provisioned.
- Confirm `CRM_JWT_SECRET` is stable across restarts.
- Check whether the account role is active and expected.

## Angular cannot reach the API

- Confirm `CRM_CORS_ALLOWED_ORIGIN` matches the frontend origin exactly.
- Confirm HTTPS certificates and DNS are valid.
- Confirm nginx proxy rules preserve `/api` paths.

## Data is missing after restart

- Confirm PostgreSQL uses persistent storage or a managed database.
- Confirm the API is pointed at the expected production database.
- Review restore and backup evidence before making changes.
