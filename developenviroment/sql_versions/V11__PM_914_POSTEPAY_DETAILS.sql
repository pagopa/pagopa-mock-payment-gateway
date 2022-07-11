INSERT INTO config(property_key, property_value)
VALUES ('POSTEPAY_PAYMENT_DETAILS_OUTCOME', 'OK');

ALTER TABLE payment_postepay ADD shop_id varchar(20);
ALTER TABLE payment_postepay ADD merchant_id varchar(20);