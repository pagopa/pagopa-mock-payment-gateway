package it.gov.pagopa.xpay.controller;

import it.gov.pagopa.xpay.dto.*;
import it.gov.pagopa.xpay.service.XPayAuth3DSService;
import it.gov.pagopa.xpay.service.XPayOrderStatusService;
import it.gov.pagopa.xpay.service.XPayPayment3DSService;
import it.gov.pagopa.xpay.service.XPayRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/xpay")
public class XPayController {
    @Autowired
    private XPayAuth3DSService xPayAuth3DSService;

    @Autowired
    private XPayPayment3DSService xPayPayment3DSService;

    @Autowired
    private XPayRefundService xPayRefundService;

    @Autowired
    private XPayOrderStatusService xPayOrderStatusService;

    @PostMapping("/ecomm/api/paga/autenticazione3DS")
    public ResponseEntity<XPayAuthResponse> xPayAuthorization(@Valid @RequestBody XPayAuthRequest request) {
       return xPayAuth3DSService.getMock(request);
    }

    @PostMapping("/ecomm/api/paga/paga3DS")
    public ResponseEntity<XPayPaymentResponse> xPayPayment(@Valid @RequestBody XPayPaymentRequest request) {
        return xPayPayment3DSService.getMock(request);
    }

    @PostMapping("/ecomm/api/bo/storna")
    public ResponseEntity<XPayRefundResponse> xPayRefund(@Valid @RequestBody XPayRefundRequest request) {
        return xPayRefundService.getMock(request);
    }

    @PostMapping("/ecomm/api/bo/situazioneOrdine")
    public ResponseEntity<XPayOrderResponse> xPayOrderStatus(@Valid @RequestBody XPayOrderRequest request) {
        return xPayOrderStatusService.getMock(request);
    }
}
