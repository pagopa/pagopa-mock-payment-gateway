INSERT INTO config(property_key, property_value)
VALUES ('POSTEPAY_ONBOARDING_OUTCOME', 'OK');

ALTER TABLE payment_postepay ADD is_onboarding CHAR(1);