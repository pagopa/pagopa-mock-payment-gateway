CREATE TABLE pp_onboarding_back(
  id            SERIAL PRIMARY KEY,
  url_return    varchar(2014) not null,
  t_timestamp   timestamp not null,
  id_appio      varchar(128) not null,
  id_back       varchar(128) not null unique,
  used          BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ON pp_onboarding_back(id_appio)WHERE NOT used
;

CREATE TABLE pp_paypal_management(
  id               SERIAL PRIMARY KEY,
  id_appio         varchar(128) not null unique,
  api_id           varchar(128) not null,
  err_code         varchar(10),
  last_update_date timestamp not null
);

CREATE TABLE user_paypal(
  id                   SERIAL PRIMARY KEY,
  id_appio             varchar(128) not null,
  paypal_email         varchar(128) not null,
  paypal_id            varchar(128) not null,
  contract_number      varchar(128) not null unique,
  creation_date        timestamp not null,
  deleted              BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX ON user_paypal(id_appio, contract_number)
;
CREATE UNIQUE INDEX ON user_paypal(id_appio)WHERE NOT DELETED
;

CREATE TABLE client(
  id                  SERIAL PRIMARY KEY,
  client_name         varchar(128) not null,
  auth_key            varchar(128) not null unique,
  creation_date       timestamp not null,
  deleted             BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX ON client(client_name)WHERE NOT DELETED
;

CREATE TABLE config(
  id                        SERIAL PRIMARY KEY,
  property_key              varchar(128) not null unique,
  property_value            varchar(128) not null
);

INSERT INTO config(property_key, property_value)
VALUES ('PAYPAL_PSP_DEFAULT_BACK_URL', 'http://localhost:8080/fallback');

INSERT INTO config(property_key, property_value)
VALUES ('PAYPAL_PSP_HMAC_KEY', 'hamac_key');

INSERT INTO client
(client_name, auth_key, creation_date, deleted)
VALUES ('local', 'local123', NOW(), false);

