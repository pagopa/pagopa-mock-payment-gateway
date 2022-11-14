package it.gov.pagopa.vpos.controller;

import it.gov.pagopa.service.ConfigService;
import it.gov.pagopa.vpos.dto.ChangeVposOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static it.gov.pagopa.vpos.utils.VposConstants.*;

@RestController
@RequestMapping(value = "/vpos/change")
public class VposSettingsController {
    @Autowired
    private ConfigService configService;

    @PostMapping("/outcome")
    public void changeVposOutcome(@Valid @RequestBody ChangeVposOutcome input) {
        configService.getOptionalByKey(VPOS_METHOD_3DS2_RESPONSE)
                .ifPresent(c -> {
                    c.setPropertyValue(input.getMethod3dsOutcome().toString());
                    configService.save(c);
                });

        configService.getOptionalByKey(VPOS_STEP0_3DS2_RESPONSE)
                .ifPresent(c -> {
                    c.setPropertyValue(input.getStep0Outcome().getCode());
                    configService.save(c);
                });

        configService.getOptionalByKey(VPOS_STEP1_3DS2_RESPONSE)
                .ifPresent(c -> {
                    c.setPropertyValue(input.getStep1Outcome().getCode());
                    configService.save(c);
                });

        configService.getOptionalByKey(VPOS_ORDER_STATUS_RESPONSE)
                .ifPresent(c -> {
                    c.setPropertyValue(input.getOrderStatusOutcome().getCode());
                    configService.save(c);
                });

        configService.getOptionalByKey(VPOS_TRANSACTION_STATUS)
                .ifPresent(c -> {
                    c.setPropertyValue(input.getTransactionStatusOutcome().getCode());
                    configService.save(c);
                });

        configService.getOptionalByKey(VPOS_HTTP_CODE_RESPONSE)
                .ifPresent(c -> {
                    c.setPropertyValue(input.getHttpOutcome().getCode());
                    configService.save(c);
                });
    }
}
