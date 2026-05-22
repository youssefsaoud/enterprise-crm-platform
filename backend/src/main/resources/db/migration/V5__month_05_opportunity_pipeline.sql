ALTER TABLE opportunities
    ADD COLUMN IF NOT EXISTS contact_id UUID REFERENCES contacts(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS currency_code CHAR(3) NOT NULL DEFAULT 'USD',
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE opportunities
    DROP CONSTRAINT IF EXISTS ck_opportunities_stage,
    ADD CONSTRAINT ck_opportunities_stage CHECK (stage IN ('PROSPECTING', 'QUALIFICATION', 'PROPOSAL', 'NEGOTIATION', 'CLOSED_WON', 'CLOSED_LOST'));

ALTER TABLE opportunities
    DROP CONSTRAINT IF EXISTS ck_opportunities_estimated_value,
    ADD CONSTRAINT ck_opportunities_estimated_value CHECK (estimated_value >= 0);

ALTER TABLE opportunities
    ADD CONSTRAINT ck_opportunities_currency_code CHECK (currency_code ~ '^[A-Z]{3}$');

CREATE INDEX IF NOT EXISTS idx_opportunities_company_id ON opportunities(company_id);
CREATE INDEX IF NOT EXISTS idx_opportunities_contact_id ON opportunities(contact_id);
CREATE INDEX IF NOT EXISTS idx_opportunities_owner_stage ON opportunities(owner_id, stage);
CREATE INDEX IF NOT EXISTS idx_opportunities_expected_close_date ON opportunities(expected_close_date);
CREATE INDEX IF NOT EXISTS idx_opportunities_name_lower ON opportunities(LOWER(name));
