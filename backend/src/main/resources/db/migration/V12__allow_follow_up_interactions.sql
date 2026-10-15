ALTER TABLE interactions
    DROP CONSTRAINT IF EXISTS ck_interactions_type,
    ADD CONSTRAINT ck_interactions_type
        CHECK (type IN ('NOTE', 'CALL', 'EMAIL', 'MEETING', 'FOLLOW_UP'));
