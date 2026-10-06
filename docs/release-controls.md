# Release Controls

The April 2024 MVP release documents the production release process. Production releases are controlled through tagged commits on `main`.

## Branch and tag policy

- `main` represents the releasable MVP line.
- Version tags use `vMAJOR.MINOR.PATCH`, for example `v1.0.0`.
- Deployments should be started only from a reviewed commit or release tag.
- Production secrets are injected by the hosting platform and are never committed.

## Manual approval points

Before a production deployment, confirm:

- CI is green for the release commit.
- Flyway migration order is reviewed.
- Database backup has completed successfully.
- Rollback plan is known and documented.
- UAT evidence is filed in `docs/acceptance-tests.md`.
