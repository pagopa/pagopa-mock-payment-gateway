INSERT INTO config(id, property_key, property_value) VALUES (1, 'PAYPAL_PSP_DEFAULT_BACK_URL', 'http://pagopa-dev:8080/pp-restapi-CD/v3/webview/paypal/fallback');
INSERT INTO config(id, property_key, property_value) VALUES (2, 'PAYPAL_PSP_HMAC_KEY', 'hmac_key');
INSERT INTO config(id, property_key, property_value) VALUES (3, 'PAYPAL_PSP_FALLBACK_PATH', '/pp-restapi-CD/v3/webview/paypal/fallback');
INSERT INTO config(id, property_key, property_value) VALUES (4, 'BPAY_PAYMENT_OUTCOME', '0');
INSERT INTO config(id, property_key, property_value) VALUES (5, 'BPAY_CURRENT_CLIENT', 'http://bancomatpay:8080');
INSERT INTO config(id, property_key, property_value) VALUES (6, 'BPAY_PAYMENT_TIMEOUT_MS', '1');
INSERT INTO config(id, property_key, property_value) VALUES (7, 'BPAY_REFUND_OUTCOME', '0');
INSERT INTO config(id, property_key, property_value) VALUES (8, 'BPAY_INQUIRY_OUTCOME', '0');
INSERT INTO config(id, property_key, property_value) VALUES (9, 'POSTEPAY_PAYMENT_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (10, 'POSTEPAY_PAYMENT_TIMEOUT_MS', '1');
INSERT INTO config(id, property_key, property_value) VALUES (11, 'POSTEPAY_REDIRECT_URL', 'http://localhost:8080');
INSERT INTO config(id, property_key, property_value) VALUES (12, 'POSTEPAY_REFUND_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (13, 'POSTEPAY_ONBOARDING_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (14, 'POSTEPAY_PAYMENT_DETAILS_OUTCOME', 'OK');

INSERT INTO client(id, client_name, auth_key, base_url) VALUES (1, 'local', 'local123', 'http://localhost:8080');