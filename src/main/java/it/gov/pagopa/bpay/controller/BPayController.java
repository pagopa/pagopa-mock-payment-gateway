package it.gov.pagopa.bpay.controller;

import it.gft.p2b.srv.pp.*;
import it.gov.pagopa.bpay.dto.*;
import org.springframework.ws.server.endpoint.annotation.*;

import javax.xml.bind.*;

@Endpoint
public class BPayController {

    private static final String NAMESPACE_URI = "http://p2b.gft.it/srv/pp";

    private static final ObjectFactory factory = new ObjectFactory();

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inquiryTransactionStatus")
    @ResponsePayload
    public JAXBElement<InquiryTransactionStatusResponse> inquiryTransactionStatus(@RequestPayload InquiryTransactionStatus request) {
        ResponseInquiryTransactionStatusVO responseData = new ResponseInquiryTransactionStatusVO();
        responseData.setEsito(generateEsito(EsitoEnum.OK));
        InquiryTransactionStatusResponse response = new InquiryTransactionStatusResponse();
        response.setReturn(responseData);
        return factory.createInquiryTransactionStatusResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inserimentoRichiestaPagamentoPagoPa")
    @ResponsePayload
    public JAXBElement<InserimentoRichiestaPagamentoPagoPaResponse> inserimentoRichiestaPagamentoPagoPa(@RequestPayload InserimentoRichiestaPagamentoPagoPa request) {
        ResponseInserimentoRichiestaPagamentoPagoPaVO responseData = new ResponseInserimentoRichiestaPagamentoPagoPaVO();
        responseData.setEsito(generateEsito(EsitoEnum.OK));
        responseData.setCorrelationId("bf6b1ac5-932f-485c-84c8-ecef7ac26461");
        InserimentoRichiestaPagamentoPagoPaResponse response = new InserimentoRichiestaPagamentoPagoPaResponse();
        response.setReturn(responseData);
        return factory.createInserimentoRichiestaPagamentoPagoPaResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "stornoPagamento")
    @ResponsePayload
    public JAXBElement<StornoPagamentoResponse> refundRequest(@RequestPayload StornoPagamento request) {
        ResponseStornoPagamentoVO responseData = new ResponseStornoPagamentoVO();
        responseData.setEsito(generateEsito(EsitoEnum.OK));
        StornoPagamentoResponse response = new StornoPagamentoResponse();
        response.setReturn(responseData);
        return factory.createStornoPagamentoResponse(response);
    }

    private EsitoVO generateEsito(EsitoEnum esitoEnum) {
        EsitoVO esito = new EsitoVO();
        esito.setEsito(esitoEnum.isEsito());
        esito.setAvvertenza(esitoEnum.isAvvertenza());
        esito.setCodice(esitoEnum.getCodice());
        esito.setMessaggio(esitoEnum.getMessaggio());
        return esito;
    }

}