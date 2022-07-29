ALTER TABLE payment_postepay ADD onboarding_transaction_id varchar(128);
ALTER TABLE payment_postepay ALTER COLUMN shop_transaction_id DROP NOT NULL;