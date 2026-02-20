ALTER TABLE companies
    ADD COLUMN IF NOT EXISTS email VARCHAR(180),
    ADD COLUMN IF NOT EXISTS address VARCHAR(255);

ALTER TABLE contacts
    DROP CONSTRAINT IF EXISTS uk_contacts_company_email;

ALTER TABLE contacts
    DROP CONSTRAINT IF EXISTS contacts_company_id_fkey,
    ADD CONSTRAINT contacts_company_id_fkey FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT;

CREATE INDEX IF NOT EXISTS idx_companies_name_lower ON companies (LOWER(name));
CREATE INDEX IF NOT EXISTS idx_companies_industry_lower ON companies (LOWER(industry));
CREATE INDEX IF NOT EXISTS idx_contacts_name_lower ON contacts (LOWER(first_name), LOWER(last_name));
CREATE INDEX IF NOT EXISTS idx_contacts_email_lower ON contacts (LOWER(email));
