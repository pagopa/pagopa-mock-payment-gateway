package it.gov.pagopa.xpay.controller;

import it.gov.pagopa.xpay.dto.XPayErrorEnum;
import it.gov.pagopa.xpay.dto.XPayOutcome;
import it.gov.pagopa.xpay.service.ConfigService;
import it.gov.pagopa.xpay.utils.XPayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static it.gov.pagopa.xpay.utils.XPayConstants.*;

@RestController
@RequestMapping("/xpay/change")
public class XPaySettingsController {
    @Autowired
    private ConfigService configService;

    @PostMapping("/autenticazione3DS/outcome")
    public void xPayAuthChangeOutcome(@RequestParam(required = false) XPayOutcome outcome,
                                      @RequestParam(required = false) String errorCode) {

        if (outcome != null)
            configService.updateXPayAuthConfig(XPAY_AUTH_OUTCOME, outcome.toString());

        if (errorCode != null) {
            XPayErrorEnum error = XPayUtils.getErrorConfig(errorCode);
            configService.updateXPayAuthConfig(XPAY_AUTH_ERROR, error.getErrorCode().toString());
        }
    }

    @PostMapping("/paga3DS/outcome")
    public void xPayPaymentChangeOutcome(@RequestParam(required = false) XPayOutcome outcome,
                                         @RequestParam(required = false) String errorCode) {

        if (outcome != null)
            configService.updateXPayAuthConfig(XPAY_PAYMENT_OUTCOME, outcome.toString());

        if (errorCode != null) {
            XPayErrorEnum error = XPayUtils.getErrorConfig(errorCode);
            configService.updateXPayAuthConfig(XPAY_PAYMENT_ERROR, error.getErrorCode().toString());
        }
    }
}
