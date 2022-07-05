INSERT INTO config(property_key, property_value)
VALUES ('POSTEPAY_REFUND_OUTCOME', 'OK');

ALTER TABLE payment_postepay ADD IS_REFUNDED CHAR(1) DEFAULT '0';