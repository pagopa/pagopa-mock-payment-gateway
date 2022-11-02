CREATE TABLE client(
  id SERIAL PRIMARY KEY,
  client_name varchar(128) not null unique,
  base_url varchar(256) not null
);

CREATE TABLE pp_onboarding_back(
  id SERIAL PRIMARY KEY,
  url_return varchar(2014) not null,
  t_timestamp timestamp not null,
  id_appio varchar(128) not null,
  id_back varchar(128) not null unique,
  used BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT unique_idappio UNIQUE (id_appio, used)
);

CREATE TABLE pp_paypal_management(
  id SERIAL PRIMARY KEY,
  id_appio varchar(128) not null,
  api_id varchar(128) not null,
  err_code varchar(10),
  last_update_date timestamp not null,
  CONSTRAINT api_un UNIQUE (id_appio, api_id)
);

CREATE TABLE user_paypal(
  id SERIAL PRIMARY KEY,
  id_appio varchar(128) not null,
  paypal_email varchar(128) not null,
  paypal_id varchar(128) not null,
  contract_number varchar(128) not null unique,
  creation_date timestamp not null,
  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT uq_idappio_contract UNIQUE (id_appio, contract_number, deleted)
);

CREATE TABLE config(
  id SERIAL PRIMARY KEY,
  property_key varchar(128) not null unique,
  property_value varchar(128) not null
);

CREATE TABLE payment_paypal(
  id SERIAL PRIMARY KEY,
  user_paypal_id integer,
  fee numeric not null,
  importo numeric not null,
  id_trs_appio varchar(128) not null unique,
  id_trs_paypal varchar(128) unique,
  esito varchar(5) not null,
  err_cod varchar(128),
  esito_refund varchar(5),
  err_cod_refund varchar(128),
  CONSTRAINT fk_user_paypal FOREIGN KEY(user_paypal_id) REFERENCES user_paypal(id)
);

CREATE TABLE payment_bpay(
	id SERIAL PRIMARY KEY,
	amount numeric not null,
	id_pagopa varchar(10) not null unique,
	id_psp varchar(10),
	outcome varchar(5) not null,
	correlation_id varchar(128) not null unique,
	refund_outcome varchar(5)
);

CREATE TABLE payment_postepay(
	id SERIAL PRIMARY KEY,
	outcome varchar(5) not null,
	shop_transaction_id varchar(128),
	payment_id varchar(128) not null,
	IS_REFUNDED BOOLEAN NOT NULL DEFAULT FALSE,
	is_onboarding boolean,
	shop_id varchar(20),
	merchant_id varchar(20),
	onboarding_transaction_id varchar(128)
);

CREATE TABLE payment_xpay(
    id SERIAL PRIMARY KEY,
    id_operazione varchar(16) not null,
    api_key varchar(50) not null,
    url_risposta varchar(50) not null,
    codice_transazione varchar(50) not null,
    importo numeric not null,
    divisa numeric not null,
    pan varchar(50),
    scadenza varchar(50),
    cvv varchar(3),
    timestamp_request timestamp not null,
    timestamp_response timestamp not null,
    mac varchar(100)
);

CREATE TABLE TRANSACTION_3DS(
    ORDER_ID varchar(50) PRIMARY KEY,
    THREE_DS_SERVER_TRANS_ID varchar(100),
    NOTIFY_URL varchar(200),
    THREE_DS_MTD_NOTIFY_URL varchar(100),
    OUTCOME varchar(50),
    THREE_DS_MTD_COMPL_IND varchar(100),
    PAN varchar(50),
    AMOUNT varchar(50),
    CURRENCY varchar(10)
);
