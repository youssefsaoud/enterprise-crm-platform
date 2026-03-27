# Month 3 Database Notes: Companies And Contacts

Month 3 builds on the Month 2 schema without editing the applied `V2__month_02_core_schema.sql` migration. New database changes live in `V3__month_03_company_contact_management.sql`.

## Migration Changes

- Adds optional `companies.email` and `companies.address` columns for the company management UI.
- Removes the Month 2 `uk_contacts_company_email` uniqueness rule so duplicate or shared contact email values are a product decision rather than an accidental database constraint.
- Changes the `contacts.company_id` foreign key from cascade delete to restrict delete behavior.
- Adds indexes for company name, company industry, contact name, and contact email search.

## Relationship Policy

```text
companies 1 ── * contacts
```

A contact must always belong to one company. Company deletion is rejected by the API while active contacts are attached, matching the Month 3 MVP policy.

## Current ERD Slice

```text
companies
- id uuid pk
- name varchar(180) not null
- industry varchar(120)
- website varchar(255)
- phone varchar(60)
- email varchar(180)
- address varchar(255)
- owner_id uuid fk app_users(id)
- created_at timestamptz
- updated_at timestamptz
- deleted_at timestamptz

contacts
- id uuid pk
- company_id uuid fk companies(id) restrict delete
- first_name varchar(80) not null
- last_name varchar(80) not null
- email varchar(180)
- phone varchar(60)
- job_title varchar(120)
- created_at timestamptz
- updated_at timestamptz
