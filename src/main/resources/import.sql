--PAYPAL CONFIGURATIONS
INSERT INTO config(id, property_key, property_value) VALUES (1, 'PAYPAL_PSP_DEFAULT_BACK_URL', '/pp-restapi-CD/v3/webview/paypal/fallback');
INSERT INTO config(id, property_key, property_value) VALUES (2, 'PAYPAL_PSP_HMAC_KEY', 'hmac_key');
INSERT INTO config(id, property_key, property_value) VALUES (3, 'PAYPAL_PSP_FALLBACK_PATH', '/pp-restapi-CD/v3/webview/paypal/fallback');

--BPAY CONFIGURATIONS
INSERT INTO config(id, property_key, property_value) VALUES (4, 'BPAY_PAYMENT_OUTCOME', '0');
INSERT INTO config(id, property_key, property_value) VALUES (5, 'BPAY_PAYMENT_TIMEOUT_MS', '1');
INSERT INTO config(id, property_key, property_value) VALUES (6, 'BPAY_REFUND_OUTCOME', '0');
INSERT INTO config(id, property_key, property_value) VALUES (7, 'BPAY_INQUIRY_OUTCOME', '0');

--POSTEPAY CONFIGURATIONS
INSERT INTO config(id, property_key, property_value) VALUES (8, 'POSTEPAY_PAYMENT_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (9, 'POSTEPAY_PAYMENT_TIMEOUT_MS', '1');
INSERT INTO config(id, property_key, property_value) VALUES (10, 'POSTEPAY_REDIRECT_URL', 'http://localhost:8080');
INSERT INTO config(id, property_key, property_value) VALUES (11, 'POSTEPAY_REFUND_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (12, 'POSTEPAY_ONBOARDING_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (13, 'POSTEPAY_PAYMENT_DETAILS_OUTCOME', 'OK');

--XPAY CONFIGURATIONS
INSERT INTO config(id, property_key, property_value) VALUES (14, 'XPAY_AUTH_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (15, 'XPAY_AUTH_ERROR', '97L');
INSERT INTO config(id, property_key, property_value) VALUES (16, 'XPAY_PAYMENT_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (17, 'XPAY_PAYMENT_ERROR', '97L');
INSERT INTO config(id, property_key, property_value) VALUES (18, 'XPAY_REFUND_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (19, 'XPAY_REFUND_ERROR', '97L');
INSERT INTO config(id, property_key, property_value) VALUES (20, 'XPAY_ORDER_STATUS_OUTCOME', 'OK');
INSERT INTO config(id, property_key, property_value) VALUES (21, 'XPAY_ORDER_STATUS_ERROR', '97L');

--VPOS CONFIGURATIONS
INSERT INTO config(id, property_key, property_value) VALUES (22, 'VPOS_METHOD_3DS2_RESPONSE','OK');
INSERT INTO config(id, property_key, property_value) VALUES (23, 'VPOS_STEP0_3DS2_RESPONSE','00');
INSERT INTO config(id, property_key, property_value) VALUES (24, 'VPOS_STEP1_3DS2_RESPONSE','00');
INSERT INTO config(id, property_key, property_value) VALUES (25, 'VPOS_ORDER_STATUS_RESPONSE','00');
INSERT INTO config(id, property_key, property_value) VALUES (26, 'VPOS_TRANSACTION_STATUS','00');
INSERT INTO config(id, property_key, property_value) VALUES (27, 'VPOS_HTTP_CODE_RESPONSE','200');

--GENERIC CONFIGURATIONS
INSERT INTO client(id, client_name, base_url) VALUES (1, 'local', 'http://host.docker.internal:8080');
INSERT INTO client(id, client_name, base_url) VALUES (2, 'sit', 'https://api.dev.platform.pagopa.it');
INSERT INTO client(id, client_name, base_url) VALUES (3, 'uat', 'https://api.uat.platform.pagopa.it');
INSERT INTO client(id, client_name, base_url) VALUES (4, 'perf', 'https://api.prf.platform.pagopa.it');