# Month 3 Database Notes: Companies And Contacts

Month 3 builds on the Month 2 schema without editing the applied `V2__month_02_core_schema.sql` migration. New database changes live in `V3__month_03_company_contact_management.sql`.

## Migration Changes

- Adds optional `companies.email` and `companies.address` columns for the company management UI.
- Removes the Month 2 `uk_contacts_company_email` uniqueness rule so duplicate or shared contact email values are a product decision rather than an accidental database constraint.
- Changes the `contacts.company_id` foreign key from cascade delete to restrict delete behavior.
- Adds indexes for company name, company industry, contact name, and contact email search.

## Relationship Policy
