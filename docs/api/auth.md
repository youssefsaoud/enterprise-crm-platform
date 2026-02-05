# Authentication API

Month 2 adds stateless JWT authentication.

## POST `/api/v1/auth/login`

Request:

```json
{"email":"admin@example.com","password":"example-password"}
```

Response:

```json
{"accessToken":"<jwt>","tokenType":"Bearer","expiresIn":3600}
```

## GET `/api/v1/auth/me`

Requires `Authorization: Bearer <jwt>` and returns a safe user profile without password hashes.

## Admin User Provisioning

Development bootstrap can create the first admin when `CRM_BOOTSTRAP_ADMIN_ENABLED=true`. After an admin exists, additional accounts can be provisioned through `POST /api/v1/admin/users` by an authenticated admin.
