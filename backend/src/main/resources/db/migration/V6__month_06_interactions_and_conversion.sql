ALTER TABLE interactions
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS follow_up_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

UPDATE interactions SET description = body WHERE description IS NULL AND body IS NOT NULL;

ALTER TABLE interactions
    DROP CONSTRAINT IF EXISTS ck_interactions_single_parent,
    ADD CONSTRAINT ck_interactions_has_parent CHECK (
        company_id IS NOT NULL OR contact_id IS NOT NULL OR lead_id IS NOT NULL OR opportunity_id IS NOT NULL
    );

ALTER TABLE leads
    DROP CONSTRAINT IF EXISTS ck_leads_status,
    ADD CONSTRAINT ck_leads_status CHECK (status IN ('NEW', 'CONTACTED', 'QUALIFIED', 'UNQUALIFIED', 'CONVERTED'));

CREATE TABLE IF NOT EXISTS lead_conversions (
    id UUID PRIMARY KEY,
    lead_id UUID NOT NULL REFERENCES leads(id) ON DELETE RESTRICT,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    contact_id UUID NOT NULL REFERENCES contacts(id) ON DELETE RESTRICT,
    opportunity_id UUID REFERENCES opportunities(id) ON DELETE SET NULL,
    converted_by_user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE RESTRICT,
    converted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_lead_conversions_lead UNIQUE (lead_id)
);

CREATE INDEX IF NOT EXISTS idx_interactions_occurred_at ON interactions(occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_interactions_type ON interactions(type);
CREATE INDEX IF NOT EXISTS idx_interactions_follow_up_at ON interactions(follow_up_at);
CREATE INDEX IF NOT EXISTS idx_interactions_company_occurred ON interactions(company_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_interactions_contact_occurred ON interactions(contact_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_interactions_lead_occurred ON interactions(lead_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_interactions_opportunity_occurred ON interactions(opportunity_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_lead_conversions_company_id ON lead_conversions(company_id);
CREATE INDEX IF NOT EXISTS idx_lead_conversions_contact_id ON lead_conversions(contact_id);
CREATE INDEX IF NOT EXISTS idx_lead_conversions_opportunity_id ON lead_conversions(opportunity_id);
