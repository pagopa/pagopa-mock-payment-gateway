INSERT INTO config(property_key, property_value) VALUES ('PAYPAL_PSP_DEFAULT_BACK_URL', 'http://pagopa-dev:8080/pp-restapi-CD/v3/webview/paypal/fallback');
INSERT INTO config(property_key, property_value) VALUES ('PAYPAL_PSP_HMAC_KEY', 'hmac_key');
INSERT INTO config(property_key, property_value) VALUES ('PAYPAL_PSP_FALLBACK_PATH', '/pp-restapi-CD/v3/webview/paypal/fallback');
INSERT INTO config(property_key, property_value) VALUES ('BPAY_PAYMENT_OUTCOME', '0');
INSERT INTO config(property_key, property_value) VALUES ('BPAY_CURRENT_CLIENT', 'http://bancomatpay:8080');
INSERT INTO config(property_key, property_value) VALUES ('BPAY_PAYMENT_TIMEOUT_MS', '1');
INSERT INTO config(property_key, property_value) VALUES ('BPAY_REFUND_OUTCOME', '0');
INSERT INTO config(property_key, property_value) VALUES ('BPAY_INQUIRY_OUTCOME', '0');
INSERT INTO config(property_key, property_value) VALUES ('POSTEPAY_PAYMENT_OUTCOME', 'OK');
INSERT INTO config(property_key, property_value) VALUES ('POSTEPAY_PAYMENT_TIMEOUT_MS', '1');
INSERT INTO config(property_key, property_value) VALUES ('POSTEPAY_REDIRECT_URL', 'http://localhost:8080');
INSERT INTO config(property_key, property_value) VALUES ('POSTEPAY_REFUND_OUTCOME', 'OK');
INSERT INTO config(property_key, property_value) VALUES ('POSTEPAY_ONBOARDING_OUTCOME', 'OK');
INSERT INTO config(property_key, property_value) VALUES ('POSTEPAY_PAYMENT_DETAILS_OUTCOME', 'OK');

INSERT INTO client(client_name, auth_key, base_url) VALUES ('local', 'local123', 'http://localhost:8080');