# Month 2 ERD

```text
app_users 1---* companies
app_users 1---* leads
app_users 1---* opportunities
app_users 1---* interactions
companies 1---* contacts
companies 1---* leads
companies 1---* opportunities
companies 1---* interactions
contacts 1---* leads
contacts 1---* interactions
leads 0..1---0..1 opportunities
leads 1---* interactions
opportunities 1---* interactions
```

The implementation keeps authentication and ownership tables in the backend while deferring business CRUD workflows to later months.
