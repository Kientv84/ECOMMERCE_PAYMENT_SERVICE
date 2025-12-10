-- Insert sample payment methods

INSERT INTO payment_method_entity (
    id, code, name, status, description, create_date, update_date
) VALUES
(
    '50aae9ae-ba5b-4627-ad7f-7546f5893bec',
    'COD',
    'Cod',
    'ACTIVE',
    'Phương thức thanh toán ship code, thanh toán khi nhận tiền ...',
    NOW(),
    NOW()
),
(
    '33e3fc19-a75b-4dda-8191-3f02c6c6d205',
    'MOMO',
    'Momo',
    'ACTIVE',
    'Phương thức thanh toán thanh toán bằng Momo payment',
    NOW(),
    NOW()
),
(
    '42818710-72dd-44c4-906b-e28eaaef859f',
    'QRCODE',
    'QR code',
    'ACTIVE',
    'Phương thức thanh toán thanh toán hình thức chuyển khoảng',
    NOW(),
    NOW()
),
(
    '5bcb642c-da7f-4607-8e86-6bbe8d7ac5df',
    'PAYPAL',
    'Paypal',
    'ACTIVE',
    'Phương thức thanh toán thanh qua paypal',
    NOW(),
    NOW()
);



-- ============================
-- Create payment based on order + processPayment logic
-- ============================

INSERT INTO payment_entity (
    id,
    order_id,
    user_id,
    order_code,
    amount,
    payment_method_id,
    status,
    payment_date,
    note,
    create_date,
    update_date
)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'c743008e-cd42-467e-bf10-35124b9a28b1',
    '99999999-9999-9999-9999-999999999999',
    'ORD-0001',
    499000.00,
    '50aae9ae-ba5b-4627-ad7f-7546f5893bec',
    'COD_PENDING',
    NULL,
    'init data payment',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);