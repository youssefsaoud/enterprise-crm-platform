ALTER TABLE leads
    ADD COLUMN IF NOT EXISTS first_name VARCHAR(80) NOT NULL DEFAULT 'Unknown',
    ADD COLUMN IF NOT EXISTS last_name VARCHAR(80) NOT NULL DEFAULT 'Lead',
    ADD COLUMN IF NOT EXISTS email VARCHAR(180),
    ADD COLUMN IF NOT EXISTS phone VARCHAR(60),
    ADD COLUMN IF NOT EXISTS company_name VARCHAR(180),
    ADD COLUMN IF NOT EXISTS created_by_id UUID REFERENCES app_users(id) ON DELETE RESTRICT,
    ADD COLUMN IF NOT EXISTS assigned_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE leads
    ALTER COLUMN first_name DROP DEFAULT,
    ALTER COLUMN last_name DROP DEFAULT;

ALTER TABLE leads
    DROP CONSTRAINT IF EXISTS ck_leads_status,
    ADD CONSTRAINT ck_leads_status CHECK (status IN ('NEW', 'CONTACTED', 'QUALIFIED', 'UNQUALIFIED'));

ALTER TABLE leads
    ADD CONSTRAINT ck_leads_source CHECK (source IN ('WEBSITE', 'REFERRAL', 'EMAIL', 'EVENT', 'OTHER'));

CREATE TABLE lead_events (
    id UUID PRIMARY KEY,
    lead_id UUID NOT NULL REFERENCES leads(id) ON DELETE CASCADE,
    event_type VARCHAR(32) NOT NULL,
    old_value VARCHAR(255),
    new_value VARCHAR(255),
    performed_by_user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_lead_events_type CHECK (event_type IN ('CREATED', 'UPDATED', 'ASSIGNED', 'UNASSIGNED', 'STATUS_CHANGED', 'DELETED'))
);

CREATE INDEX IF NOT EXISTS idx_leads_assigned_status ON leads(assigned_to_id, status);
CREATE INDEX IF NOT EXISTS idx_leads_created_at ON leads(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_leads_company_id ON leads(company_id);
CREATE INDEX IF NOT EXISTS idx_leads_created_by_id ON leads(created_by_id);
CREATE INDEX IF NOT EXISTS idx_leads_name_email_lower ON leads(LOWER(first_name), LOWER(last_name), LOWER(email));
CREATE INDEX IF NOT EXISTS idx_lead_events_lead_created ON lead_events(lead_id, created_at DESC);
