ALTER TABLE interactions
    ADD COLUMN IF NOT EXISTS project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    ADD COLUMN IF NOT EXISTS project_vendor_id UUID REFERENCES project_vendors(id) ON DELETE CASCADE,
    ADD COLUMN IF NOT EXISTS quotation_id UUID REFERENCES quotations(id) ON DELETE SET NULL;

ALTER TABLE interactions
    DROP CONSTRAINT IF EXISTS ck_interactions_has_parent,
    ADD CONSTRAINT ck_interactions_has_parent CHECK (
        company_id IS NOT NULL OR contact_id IS NOT NULL OR lead_id IS NOT NULL OR opportunity_id IS NOT NULL
        OR project_id IS NOT NULL OR project_vendor_id IS NOT NULL OR quotation_id IS NOT NULL
    );

CREATE INDEX idx_interactions_project_occurred ON interactions(project_id, occurred_at DESC);
CREATE INDEX idx_interactions_project_vendor_occurred ON interactions(project_vendor_id, occurred_at DESC);
CREATE INDEX idx_interactions_quotation ON interactions(quotation_id);
