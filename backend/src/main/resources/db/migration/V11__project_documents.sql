CREATE TABLE project_documents (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    quotation_id UUID REFERENCES quotations(id) ON DELETE CASCADE,
    original_filename VARCHAR(255) NOT NULL,
    storage_key VARCHAR(120) NOT NULL UNIQUE,
    content_type VARCHAR(120) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_by UUID NOT NULL REFERENCES app_users(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_project_documents_size CHECK (file_size > 0 AND file_size <= 10485760),
    CONSTRAINT ck_project_documents_filename CHECK (length(trim(original_filename)) > 0)
);

CREATE INDEX idx_project_documents_project ON project_documents(project_id, created_at DESC);
CREATE INDEX idx_project_documents_quotation ON project_documents(quotation_id);
