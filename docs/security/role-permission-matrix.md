# Role Permission Matrix

| Capability | Admin | Manager | Sales Rep |
| --- | --- | --- | --- |
| Sign in and view own account | Yes | Yes | Yes |
| Provision users | Yes | No | No |
| View admin-only health of RBAC | Yes | No | No |
| View manager-scoped endpoint | Yes | Yes | No |
| View sales-scoped endpoint | Yes | Yes | Yes |

Angular guards improve navigation, but Spring Security remains the source of authorization truth.
