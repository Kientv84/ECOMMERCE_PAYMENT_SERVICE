-- ============================
-- Create table: payment_method_entity
-- ============================
CREATE TABLE payment_method_entity (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(30) NOT NULL,

    -- audit fields
    create_date TIMESTAMP,
    update_date TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- ============================
-- Create table: payment_entity
-- ============================
CREATE TABLE payment_entity (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    user_id UUID NOT NULL,
    order_code VARCHAR(255) NOT NULL,

    amount NUMERIC(19, 2) NOT NULL,

    paymentMethod_id UUID,                      -- FK to payment_method_entity
    transaction_code VARCHAR(255) UNIQUE,

    status VARCHAR(30) NOT NULL,
    payment_date TIMESTAMP,
    note VARCHAR(255),

    -- audit fields
    create_date TIMESTAMP,
    update_date TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_payment_method
        FOREIGN KEY (paymentMethod_id)
        REFERENCES payment_method_entity(id)
);

-- ============================
-- Tạo quan hệ ngược từ payment_method → payment (1-1 optional)
-- ============================
ALTER TABLE payment_method_entity
ADD COLUMN payment_id UUID;

ALTER TABLE payment_method_entity
ADD CONSTRAINT fk_method_payment
    FOREIGN KEY (payment_id)
    REFERENCES payment_entity(id);
