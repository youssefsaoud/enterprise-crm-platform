CREATE TABLE crm_schema_metadata (
    id BIGSERIAL PRIMARY KEY,
    schema_version VARCHAR(32) NOT NULL,
    description VARCHAR(255) NOT NULL,
    applied_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO crm_schema_metadata (schema_version, description)
VALUES ('0.1.0', 'Month 1 technical foundation baseline');
