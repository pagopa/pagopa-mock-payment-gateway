package it.gov.pagopa.xpay.controller;

import it.gov.pagopa.xpay.dto.XPayOutcome;
import it.gov.pagopa.xpay.service.ConfigService;
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

        configService.updateXPayOutcomeAndError(XPAY_AUTH_OUTCOME, outcome, XPAY_AUTH_ERROR, errorCode);
    }

    @PostMapping("/paga3DS/outcome")
    public void xPayPaymentChangeOutcome(@RequestParam(required = false) XPayOutcome outcome,
                                         @RequestParam(required = false) String errorCode) {

        configService.updateXPayOutcomeAndError(XPAY_PAYMENT_OUTCOME, outcome, XPAY_PAYMENT_ERROR, errorCode);
    }

    @PostMapping("/storna/outcome")
    public void xPayRefundChangeOutcome(@RequestParam(required = false) XPayOutcome outcome,
                                        @RequestParam(required = false) String errorCode) {

        configService.updateXPayOutcomeAndError(XPAY_REFUND_OUTCOME, outcome, XPAY_REFUND_ERROR, errorCode);
    }

    @PostMapping("/situazioneOrdine/outcome")
    public void xPayOrderStatusChangeOutcome(@RequestParam(required = false) XPayOutcome outcome,
                                             @RequestParam(required = false) String errorCode) {

        configService.updateXPayOutcomeAndError(XPAY_ORDER_STATUS_OUTCOME, outcome, XPAY_ORDER_STATUS_ERROR, errorCode);
    }
}
