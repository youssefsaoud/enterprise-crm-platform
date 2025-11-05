# Roles And Permissions

This matrix documents the planned authorization model. Implementation is deferred until authentication and role-based access control are added.

| Capability | Admin | Manager | Sales Representative |
| --- | --- | --- | --- |
| View dashboard | Yes | Yes | Yes |
| Manage users | Yes | No | No |
| View companies | Yes | Yes | Yes |
| Create companies | Yes | Yes | Yes |
| Edit companies | Yes | Yes | Own or assigned records |
| Delete companies | Yes | No | No |
| View contacts | Yes | Yes | Yes |
| Create contacts | Yes | Yes | Yes |
| Edit contacts | Yes | Yes | Own or assigned records |
| Delete contacts | Yes | No | No |
| View leads | Yes | Team leads | Own or assigned leads |
| Create leads | Yes | Yes | Yes |
| Assign leads | Yes | Yes | No |
| Qualify leads | Yes | Yes | Own or assigned leads |
| Disqualify leads | Yes | Yes | Own or assigned leads |
| Convert leads | Yes | Yes | Own or assigned leads |
| View opportunities | Yes | Team opportunities | Own or assigned opportunities |
| Move opportunity stage | Yes | Yes | Own or assigned opportunities |
| Delete opportunities | Yes | No | No |
| Log activities | Yes | Yes | Yes |

## Notes

- "Own or assigned" means the current user is the record owner or has been assigned the record.
- Team visibility requires a manager-to-representative relationship that will be modeled later.
- Destructive operations should be restricted and audited.
