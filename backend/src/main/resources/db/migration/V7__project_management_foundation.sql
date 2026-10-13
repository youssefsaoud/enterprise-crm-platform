CREATE TABLE company_roles (
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    role VARCHAR(16) NOT NULL,
    PRIMARY KEY (company_id, role),
    CONSTRAINT ck_company_roles_role CHECK (role IN ('CLIENT', 'VENDOR'))
);

INSERT INTO company_roles (company_id, role)
SELECT id, 'CLIENT'
FROM companies
WHERE deleted_at IS NULL
ON CONFLICT DO NOTHING;

CREATE INDEX idx_company_roles_role ON company_roles(role);

CREATE TABLE projects (
    id UUID PRIMARY KEY,
    name VARCHAR(180) NOT NULL,
    reference VARCHAR(40) NOT NULL,
    client_id UUID NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    description VARCHAR(4000),
    category VARCHAR(120),
    location VARCHAR(255),
    deadline DATE,
    estimated_budget NUMERIC(14, 2),
    currency_code VARCHAR(3) NOT NULL DEFAULT 'USD',
    assigned_associate_id UUID REFERENCES app_users(id) ON DELETE SET NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PLANNED',
    scope_of_work TEXT,
    notes VARCHAR(4000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT uk_projects_reference UNIQUE (reference),
    CONSTRAINT ck_projects_status CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT ck_projects_budget CHECK (estimated_budget IS NULL OR estimated_budget >= 0),
    CONSTRAINT ck_projects_currency_code CHECK (currency_code ~ '^[A-Z]{3}$')
);

CREATE INDEX idx_projects_client_id ON projects(client_id);
CREATE INDEX idx_projects_assigned_associate_id ON projects(assigned_associate_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_deadline ON projects(deadline);
CREATE INDEX idx_projects_name_lower ON projects(LOWER(name));