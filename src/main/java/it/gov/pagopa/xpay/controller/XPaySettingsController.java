package it.gov.pagopa.xpay.controller;

import it.gov.pagopa.db.entity.TableConfig;
import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.xpay.dto.EsitoXpay;
import it.gov.pagopa.xpay.dto.XPayErrorEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_AUTH_ERROR;
import static it.gov.pagopa.xpay.utils.XPayConstants.XPAY_AUTH_OUTCOME;

@Validated
@RestController
@RequestMapping("/xpay/change")
@Log4j2
public class XPaySettingsController {
    @Autowired
    private TableConfigRepository configRepository;

    @PostMapping("/autenticazione3DS/outcome")
    public void changeOutcome(@RequestParam(required = false) EsitoXpay outcome,
                              @RequestParam(required = false) Long codiceErrore) {

        if (outcome != null) {
            TableConfig outcomeConfig = configRepository.findByPropertyKey(XPAY_AUTH_OUTCOME);
            outcomeConfig.setPropertyValue(outcome.toString());
            configRepository.save(outcomeConfig);
        }

        if (codiceErrore != null) {
            TableConfig errorConfig = configRepository.findByPropertyKey(XPAY_AUTH_ERROR);

            //Se il valore mandato in input non è un codice valido, salvo "Errore Generico" come dafault
            try {
                XPayErrorEnum.valueOf("ERROR_" + codiceErrore);
            } catch (IllegalArgumentException e) {
                codiceErrore = 97L;
            }

            errorConfig.setPropertyValue(codiceErrore.toString());
            configRepository.save(errorConfig);
        }
    }
}
