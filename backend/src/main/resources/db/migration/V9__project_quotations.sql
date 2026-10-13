CREATE TABLE quotations (
    id UUID PRIMARY KEY,
    project_vendor_id UUID NOT NULL REFERENCES project_vendors(id) ON DELETE CASCADE,
    amount NUMERIC(14, 2) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    received_date DATE NOT NULL,
    valid_until DATE,
    status VARCHAR(32) NOT NULL DEFAULT 'RECEIVED',
    revision_number INTEGER NOT NULL,
    selected BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(4000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_quotations_amount CHECK (amount >= 0),
    CONSTRAINT ck_quotations_currency CHECK (currency_code ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_quotations_status CHECK (status IN ('RECEIVED', 'UNDER_REVIEW', 'REVISED', 'REJECTED', 'SELECTED', 'EXPIRED', 'WITHDRAWN')),
    CONSTRAINT ck_quotations_revision CHECK (revision_number > 0),
    CONSTRAINT ck_quotations_dates CHECK (valid_until IS NULL OR valid_until >= received_date)
);

CREATE UNIQUE INDEX uk_quotations_vendor_revision ON quotations(project_vendor_id, revision_number);
CREATE UNIQUE INDEX uk_quotations_selected_project_vendor ON quotations(project_vendor_id) WHERE selected = TRUE;
CREATE INDEX idx_quotations_project_vendor ON quotations(project_vendor_id);
CREATE INDEX idx_quotations_status ON quotations(status);
CREATE INDEX idx_quotations_received_date ON quotations(received_date);
