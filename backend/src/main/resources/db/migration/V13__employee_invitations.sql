CREATE TABLE employee_invitations (
    id UUID PRIMARY KEY,
    email VARCHAR(180) NOT NULL,
    normalized_email VARCHAR(180) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    role VARCHAR(32) NOT NULL CHECK (role IN ('MANAGER', 'SALES_REP')),
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    status VARCHAR(16) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'REVOKED')),
    delivery_status VARCHAR(16) NOT NULL CHECK (delivery_status IN ('SENDING', 'SENT', 'FAILED', 'PREVIEW')),
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    accepted_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    last_sent_at TIMESTAMPTZ NOT NULL,
    delivery_attempts INTEGER NOT NULL DEFAULT 1 CHECK (delivery_attempts > 0),
    invited_by UUID NOT NULL REFERENCES app_users(id) ON DELETE RESTRICT,
    CHECK (expires_at > created_at),
    CHECK ((status = 'ACCEPTED') = (accepted_at IS NOT NULL)),
    CHECK ((status = 'REVOKED') = (revoked_at IS NOT NULL))
);
CREATE UNIQUE INDEX uk_employee_invitations_pending_email
    ON employee_invitations(normalized_email) WHERE status = 'PENDING';
CREATE INDEX idx_employee_invitations_created ON employee_invitations(created_at DESC);
