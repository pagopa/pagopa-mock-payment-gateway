package it.gov.pagopa.xpay.controller;

import it.gov.pagopa.xpay.dto.*;
import it.gov.pagopa.xpay.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;

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

    @Autowired
    private XPayMacService xPayMacService;

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

    @GetMapping("/mac")
    public ResponseEntity<XPayAuthResponse> generateMac(@RequestParam String codiceTransazione, @RequestParam String timeStamp,
                                              @RequestParam(required = false) Long divisa,
                                              @RequestParam(required = false) BigInteger importo) {

        return xPayMacService.getMac(codiceTransazione, divisa, importo, timeStamp);
    }
}
