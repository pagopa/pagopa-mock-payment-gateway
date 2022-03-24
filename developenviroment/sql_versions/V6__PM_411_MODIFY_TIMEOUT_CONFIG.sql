DELETE FROM config WHERE property_key = 'BPAY_PAYMENT_TIMEOUT';
INSERT INTO config(property_key, property_value)
VALUES ('BPAY_PAYMENT_TIMEOUT_MS', '5000');