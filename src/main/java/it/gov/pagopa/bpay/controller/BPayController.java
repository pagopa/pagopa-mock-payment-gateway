package it.gov.pagopa.bpay.controller;

import it.gft.p2b.srv.pp.*;
import org.springframework.ws.server.endpoint.annotation.*;

@Endpoint
public class BPayController {

    private static final String NAMESPACE_URI = "http://p2b.gft.it/srv/pp";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inquiryTransactionStatus")
    @ResponsePayload
    public ResponseInquiryTransactionStatusVO transactionStatusRequest(@RequestPayload RequestInquiryTransactionStatusVO request) {
        ResponseInquiryTransactionStatusVO response = new ResponseInquiryTransactionStatusVO();
        response.setEndToEndId(null);
        response.setEsitoPagamento(null);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inserimentoRichiestaPagamentoPagoPa")
    @ResponsePayload
    public ResponseInserimentoRichiestaPagamentoPagoPaVO paymentRequest(@RequestPayload RequestInserimentoRichiestaPagamentoPagoPaVO request) {
        ResponseInserimentoRichiestaPagamentoPagoPaVO response = new ResponseInserimentoRichiestaPagamentoPagoPaVO();
        response.setCorrelationId(null);
        response.setContesto(null);
        response.setEsito(null);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "stornoPagamento")
    @ResponsePayload
    public ResponseStornoPagamentoVO refundRequest(@RequestPayload RequestStornoPagamentoVO request) {
        ResponseStornoPagamentoVO response = new ResponseStornoPagamentoVO();
        response.setEndToEndIdStorno(null);
        response.setContesto(null);
        response.setEsito(null);
        return response;
    }

}