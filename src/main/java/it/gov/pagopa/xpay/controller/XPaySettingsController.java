package it.gov.pagopa.xpay.controller;

import it.gov.pagopa.db.entity.TableConfig;
import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.xpay.dto.EsitoXpay;
import it.gov.pagopa.xpay.dto.XPayErrorEnum;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.EnumUtils;
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
                              @RequestParam(required = false) Long errorCode) {

        if (outcome != null) {
            TableConfig outcomeConfig = configRepository.findByPropertyKey(XPAY_AUTH_OUTCOME);
            outcomeConfig.setPropertyValue(outcome.toString());
            configRepository.save(outcomeConfig);
        }

        if (errorCode != null) {
            TableConfig errorConfig = configRepository.findByPropertyKey(XPAY_AUTH_ERROR);

            //If the errorCode is invalid, a Generic Error (ERROR_97) is saved on the database.
            //If it's valid, the correct XPayErrorEnum is saved.
            if(!EnumUtils.isValidEnum(XPayErrorEnum.class, "ERROR_" + errorCode)) {
                errorCode = XPayErrorEnum.ERROR_97.getErrorCode();
            }

            errorConfig.setPropertyValue(errorCode.toString());
            configRepository.save(errorConfig);
        }
    }
}
