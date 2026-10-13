CREATE TABLE project_vendors (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    vendor_id UUID NOT NULL REFERENCES companies(id) ON DELETE RESTRICT,
    responsible_associate_id UUID REFERENCES app_users(id) ON DELETE SET NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'NOT_CONTACTED',
    first_contact_date DATE,
    last_contact_date DATE,
    next_follow_up_date DATE,
    notes VARCHAR(4000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT ck_project_vendors_status CHECK (status IN ('NOT_CONTACTED', 'NO_CONTACT_REACHED', 'SOW_SENT', 'QUOTE_IN_PROCESS', 'QUOTE_RECEIVED', 'QUOTE_DECLINED')),
    CONSTRAINT ck_project_vendors_dates CHECK (last_contact_date IS NULL OR first_contact_date IS NULL OR last_contact_date >= first_contact_date)
);

CREATE UNIQUE INDEX uk_project_vendors_active_assignment
    ON project_vendors(project_id, vendor_id)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_project_vendors_project ON project_vendors(project_id);
CREATE INDEX idx_project_vendors_vendor ON project_vendors(vendor_id);
CREATE INDEX idx_project_vendors_status ON project_vendors(status);
CREATE INDEX idx_project_vendors_follow_up ON project_vendors(next_follow_up_date)
    WHERE deleted_at IS NULL;
