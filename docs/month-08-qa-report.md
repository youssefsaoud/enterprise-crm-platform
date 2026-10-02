# Month 8 QA Report: Testing, Security And Stabilization

Month 8 focuses on quality and release readiness rather than new CRM modules.

## Automated Coverage Added

- Backend unit coverage for dashboard summary aggregation, open pipeline totals, and recent activity mapping.
- Angular service coverage for dashboard HTTP endpoints.
- Angular component coverage for dashboard loading and error states.

## Checks Run

| Check | Result | Notes |
|---|---|---|
| Backend `mvn verify` | Blocked | Maven is not installed or available on PATH, and no Maven wrapper exists in the repository. |
| Frontend production build | Passed with warnings | Build completed. Existing CSS warnings recommend `flex-end` instead of `end` in older feature styles. |
| Angular unit tests | Blocked after bundle compile | Karma could not start ChromeHeadless because Chrome was not found and `CHROME_BIN` is not configured. |

## Build Warnings

Angular build completed successfully, but Autoprefixer reported existing compatibility warnings for `align-items: end` in company, contact, lead, and opportunity list styles.

## Security Review Summary

- JWT is applied by a stateless Spring Security filter.
- `/api/v1/health` and `/api/v1/auth/login` are public; other endpoints require authentication.
- Admin routes are restricted with `hasRole("ADMIN")`.
- Object-level authorization remains implemented in service-layer rules for leads, opportunities, interactions, conversion, and dashboard scoping.
- Manager scope is still broad by MVP policy because no manager-team membership model exists yet.

## Open Release Risks

- Backend test execution requires Maven or a committed Maven wrapper.
- Frontend unit test execution requires a configured headless browser.
- Month 8 should be rerun in an environment with Maven and ChromeHeadless before production deployment preparation.
