ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS email VARCHAR(180),
    ADD COLUMN IF NOT EXISTS address VARCHAR(255);

ALTER TABLE contacts
    DROP CONSTRAINT IF EXISTS uk_contacts_company_email;

ALTER TABLE contacts
    DROP CONSTRAINT IF EXISTS contacts_company_id_fkey,
    ADD CONSTRAINT contacts_company_id_fkey FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT;
