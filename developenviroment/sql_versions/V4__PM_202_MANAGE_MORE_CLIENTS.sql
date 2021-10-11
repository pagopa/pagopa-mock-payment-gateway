delete from payment_paypal;
delete from pp_onboarding_back;
delete from user_paypal;
delete from pp_paypal_management;

ALTER TABLE pm_mock_local.pp_onboarding_back ADD client_id integer NOT NULL;
ALTER TABLE pp_onboarding_back ADD CONSTRAINT fk_pob_client FOREIGN KEY(client_id) REFERENCES client(id);

ALTER TABLE pm_mock_local.user_paypal ADD client_id integer NOT NULL;
ALTER TABLE user_paypal ADD CONSTRAINT fk_up_client FOREIGN KEY(client_id) REFERENCES client(id);

ALTER TABLE pm_mock_local.pp_paypal_management ADD client_id integer NOT NULL;
ALTER TABLE pp_paypal_management ADD CONSTRAINT fk_ppm_client FOREIGN KEY(client_id) REFERENCES client(id);

CREATE UNIQUE INDEX ON user_paypal(id_appio, client_id)WHERE NOT DELETED;
CREATE UNIQUE INDEX ON user_paypal(id_appio, client_id, contract_number)WHERE NOT DELETED;

DROP INDEX pm_mock_local.user_paypal_id_appio_contract_number_idx;
DROP INDEX pm_mock_local.user_paypal_id_appio_idx;

ALTER TABLE pm_mock_local.client ADD base_url varchar(256);
ALTER TABLE pm_mock_local.client ADD CONSTRAINT client_un UNIQUE (client_name);

UPDATE pm_mock_local.client SET base_url='http://localhost:8080' WHERE client_name='local';