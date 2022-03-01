INSERT INTO config(property_key, property_value)
VALUES ('BPAY_CALLBACK_BASE_PATH', '/pp-restapi-CD/v3');

INSERT INTO config(property_key, property_value)
VALUES ('BPAY_PAYMENT_OUTCOME', '0');

INSERT INTO config(property_key, property_value)
VALUES ('BPAY_PAYMENT_TIMEOUT', 'false');

CREATE TABLE payment_bpay(
	id SERIAL PRIMARY KEY,
	amount numeric not null,
	id_pagopa varchar(10) not null unique,
	id_psp varchar(10),
	outcome varchar(5) not null,
	correlation_id varchar(128) not null unique,
	refund_outcome varchar(5),
	client_hostname varchar(128)
);