CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE EXTENSION IF NOT EXISTS "pgcrypto";
-- =========================================================
-- TABLE: payment_method_entity
-- =========================================================
DROP TABLE IF EXISTS payment_method_entity CASCADE;
CREATE TABLE payment_method_entity (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(30) NOT NULL,

    -- audit fields
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- =========================================================
-- TABLE: payment_entity
-- =========================================================
DROP TABLE IF EXISTS payment_entity CASCADE;
CREATE TABLE payment_entity (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    user_id UUID NOT NULL,
    order_code VARCHAR(255) NOT NULL,

    amount NUMERIC(19, 2) NOT NULL,

    payment_method_id UUID,   -- FK
    transaction_code VARCHAR(255) UNIQUE,

    status VARCHAR(30) NOT NULL,
    payment_date TIMESTAMP,
    note VARCHAR(255),

    -- audit fields
    create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_payment_method
        FOREIGN KEY (payment_method_id)
        REFERENCES payment_method_entity(id)
);

-- Index cho FK
CREATE INDEX IF NOT EXISTS idx_payment_payment_method_id
    ON payment_entity(payment_method_id);

-- =========================================================
-- FULL TEXT SEARCH — PAYMENT METHOD
-- =========================================================

ALTER TABLE payment_method_entity
ADD COLUMN IF NOT EXISTS document_tsv tsvector;

UPDATE payment_method_entity
SET document_tsv = to_tsvector(
    'simple',
      coalesce(public.unaccent(lower(name)), '') || ' '
    || coalesce(public.unaccent(lower(code)), '') || ' '
    || coalesce(public.unaccent(lower(description)), '')
);

CREATE INDEX IF NOT EXISTS idx_payment_method_tsv
    ON payment_method_entity USING GIN(document_tsv);

CREATE OR REPLACE FUNCTION payment_method_tsv_trigger()
RETURNS trigger AS $$
BEGIN
    NEW.document_tsv := to_tsvector(
        'simple',
          coalesce(public.unaccent(lower(NEW.name)), '') || ' '
        || coalesce(public.unaccent(lower(NEW.code)), '') || ' '
        || coalesce(public.unaccent(lower(NEW.description)), '')
    );
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_payment_method_tsv ON payment_method_entity;

CREATE TRIGGER trg_payment_method_tsv
BEFORE INSERT OR UPDATE ON payment_method_entity
FOR EACH ROW EXECUTE FUNCTION payment_method_tsv_trigger();


CREATE OR REPLACE FUNCTION payment_method_search_suggest(input_text text, limit_count int DEFAULT 5)
RETURNS TABLE (
    payment_method_id uuid,
    name text,
    code text,
    description text,
    rank float
) AS $$
DECLARE
    q text;
BEGIN
    q := public.unaccent(lower(coalesce(input_text, '')));

    -- Nếu chuỗi rỗng → trả về rỗng tránh lỗi tsquery
    IF q = '' THEN
        RETURN;
    END IF;

    -- sanitize input tránh lỗi với dấu : & |
    q := regexp_replace(q, '[:&|!]', ' ', 'g');

    RETURN QUERY
    SELECT
        id AS payment_method_id,
        name,
        code,
        description,
        ts_rank(document_tsv, to_tsquery('simple', q || ':*')) AS rank
    FROM payment_method_entity
    WHERE document_tsv @@ to_tsquery('simple', q || ':*')
    ORDER BY rank DESC
    LIMIT limit_count;
END;
$$ LANGUAGE plpgsql;


-- =========================================================
-- FULL TEXT SEARCH — PAYMENT ENTITY
-- =========================================================

ALTER TABLE payment_entity
ADD COLUMN IF NOT EXISTS document_tsv tsvector;

UPDATE payment_entity
SET document_tsv = to_tsvector(
    'simple',
    coalesce(public.unaccent(lower(transaction_code)), '')
);

CREATE INDEX IF NOT EXISTS idx_payment_tsv
    ON payment_entity USING GIN(document_tsv);

CREATE OR REPLACE FUNCTION payment_tsv_trigger()
RETURNS trigger AS $$
BEGIN
    NEW.document_tsv := to_tsvector(
        'simple',
        coalesce(public.unaccent(lower(NEW.transaction_code)), '')
    );
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_payment_tsv ON payment_entity;

CREATE TRIGGER trg_payment_tsv
BEFORE INSERT OR UPDATE ON payment_entity
FOR EACH ROW EXECUTE FUNCTION payment_tsv_trigger();


CREATE OR REPLACE FUNCTION payment_search_suggest(input_text text, limit_count int DEFAULT 5)
RETURNS TABLE (
    payment_id uuid,
    transaction_code text,
    rank float
) AS $$
DECLARE
    q text;
BEGIN
    q := public.unaccent(lower(coalesce(input_text, '')));

    IF q = '' THEN
        RETURN;
    END IF;

    q := regexp_replace(q, '[:&|!]', ' ', 'g');

    RETURN QUERY
    SELECT
        id AS payment_id,
        transaction_code,
        ts_rank(document_tsv, to_tsquery('simple', q || ':*')) AS rank
    FROM payment_entity
    WHERE document_tsv @@ to_tsquery('simple', q || ':*')
    ORDER BY rank DESC
    LIMIT limit_count;
END;
$$ LANGUAGE plpgsql;