CREATE TABLE IF NOT EXISTS urls (
    id         SERIAL    PRIMARY KEY,
    short_code VARCHAR(8) NOT NULL UNIQUE,
    long_url   TEXT       NOT NULL,
    created_at TIMESTAMP  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_urls_short_code
    ON urls(short_code);
