CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    normalized_email VARCHAR(180) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_app_users_normalized_email UNIQUE (normalized_email),
    CONSTRAINT ck_app_users_role CHECK (role IN ('ADMIN', 'MANAGER', 'SALES_REP'))
);

CREATE TABLE companies (
    id UUID PRIMARY KEY,
    name VARCHAR(180) NOT NULL,
    industry VARCHAR(120),
    website VARCHAR(255),
    phone VARCHAR(60),
    owner_id UUID REFERENCES app_users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE contacts (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    email VARCHAR(180),
    phone VARCHAR(60),
    job_title VARCHAR(120),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT uk_contacts_company_email UNIQUE (company_id, email)
);

CREATE TABLE leads (
    id UUID PRIMARY KEY,
    company_id UUID REFERENCES companies(id) ON DELETE SET NULL,
    contact_id UUID REFERENCES contacts(id) ON DELETE SET NULL,
    assigned_to_id UUID REFERENCES app_users(id) ON DELETE SET NULL,
    source VARCHAR(80),
    status VARCHAR(32) NOT NULL,
    summary VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_leads_status CHECK (status IN ('NEW', 'CONTACTED', 'QUALIFIED', 'DISQUALIFIED'))
);

CREATE TABLE opportunities (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    owner_id UUID REFERENCES app_users(id) ON DELETE SET NULL,
    lead_id UUID UNIQUE REFERENCES leads(id) ON DELETE SET NULL,
    stage VARCHAR(32) NOT NULL,
    name VARCHAR(180) NOT NULL,
    estimated_value NUMERIC(14, 2) NOT NULL DEFAULT 0,
    expected_close_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT ck_opportunities_stage CHECK (stage IN ('PROSPECTING', 'QUALIFICATION', 'PROPOSAL', 'NEGOTIATION', 'WON', 'LOST')),
    CONSTRAINT ck_opportunities_estimated_value CHECK (estimated_value >= 0)
);

CREATE TABLE interactions (
    id UUID PRIMARY KEY,
    author_id UUID NOT NULL REFERENCES app_users(id) ON DELETE RESTRICT,
    company_id UUID REFERENCES companies(id) ON DELETE CASCADE,
    contact_id UUID REFERENCES contacts(id) ON DELETE CASCADE,
    lead_id UUID REFERENCES leads(id) ON DELETE CASCADE,
    opportunity_id UUID REFERENCES opportunities(id) ON DELETE CASCADE,
    type VARCHAR(32) NOT NULL,
    subject VARCHAR(180) NOT NULL,
    body TEXT,
    occurred_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_interactions_type CHECK (type IN ('NOTE', 'CALL', 'EMAIL', 'MEETING')),
    CONSTRAINT ck_interactions_single_parent CHECK (
        ((company_id IS NOT NULL)::integer + (contact_id IS NOT NULL)::integer + (lead_id IS NOT NULL)::integer + (opportunity_id IS NOT NULL)::integer) = 1
    )
);

CREATE INDEX idx_companies_owner_id ON companies(owner_id);
CREATE INDEX idx_contacts_company_id ON contacts(company_id);
CREATE INDEX idx_leads_assigned_to_id ON leads(assigned_to_id);
CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_opportunities_owner_id ON opportunities(owner_id);
CREATE INDEX idx_opportunities_stage ON opportunities(stage);
CREATE INDEX idx_interactions_author_id ON interactions(author_id);
CREATE INDEX idx_interactions_company_id ON interactions(company_id);
CREATE INDEX idx_interactions_contact_id ON interactions(contact_id);
CREATE INDEX idx_interactions_lead_id ON interactions(lead_id);
CREATE INDEX idx_interactions_opportunity_id ON interactions(opportunity_id);
