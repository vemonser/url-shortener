-- ═══════════════════════════════════════════════
-- PRIMARY DATABASE
-- ═══════════════════════════════════════════════

CREATE TABLE short_keys (
    id         SERIAL       PRIMARY KEY,
    short_code VARCHAR(8)   NOT NULL UNIQUE,
    is_mapped  BOOLEAN      NOT NULL DEFAULT false
);

 
CREATE INDEX idx_keys_unmapped
    ON short_keys(is_mapped)
    WHERE is_mapped = false;

-- ═══════════════════════════════════════════════
-- SHARD DATABASES 
-- ═══════════════════════════════════════════════

CREATE TABLE urls (
    id         SERIAL       PRIMARY KEY,
    short_code VARCHAR(8)   NOT NULL UNIQUE,
    long_url   TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);


CREATE INDEX idx_urls_short_code
    ON urls(short_code);