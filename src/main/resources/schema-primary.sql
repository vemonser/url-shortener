CREATE TABLE IF NOT EXISTS short_keys (
    id         SERIAL     PRIMARY KEY,
    short_code VARCHAR(8) NOT NULL UNIQUE,
    is_mapped  BOOLEAN    NOT NULL DEFAULT false
);

CREATE INDEX IF NOT EXISTS idx_keys_unmapped
    ON short_keys(is_mapped)
    WHERE is_mapped = false;