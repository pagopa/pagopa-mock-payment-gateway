INSERT INTO config(property_key, property_value)
VALUES ('BPAY_CALLBACK_BASE_PATH', 'http://pagopa-dev:8080/pp-restapi-CD/v3');

CREATE TABLE payment_bpay(
	id SERIAL PRIMARY KEY,
	amount numeric not null,
	id_pagopa varchar(128) not null unique,
	id_psp varchar(10),
	outcome varchar(5) not null,
	correlation_id varchar(128),
	refunded numeric default 0
);
