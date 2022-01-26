package it.gov.pagopa.bpay.controller;

import it.gft.p2b.srv.pp.*;
import it.gov.pagopa.bpay.client.*;
import it.gov.pagopa.bpay.dto.*;
import it.gov.pagopa.bpay.entity.*;
import it.gov.pagopa.bpay.repository.*;
import it.gov.pagopa.db.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.ws.server.endpoint.annotation.*;

import javax.annotation.*;
import javax.xml.bind.*;
import java.lang.*;
import java.lang.Exception;
import java.util.*;

@Endpoint
public class BPayController {

    @Autowired
    private TableConfigRepository tableConfigRepository;

    @Autowired
    private BPayPaymentRepository paymentRepository;

    @PostConstruct
    public void init() {
        String pmBaseUrl = tableConfigRepository.findByPropertyKey("BPAY_CALLBACK_BASE_PATH").getPropertyValue();
        pmClient = new PmClientImpl(pmBaseUrl);
    }

    private static PmClientImpl pmClient;

    private static final String NAMESPACE_URI = "http://p2b.gft.it/srv/pp";

    private static final ObjectFactory factory = new ObjectFactory();

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inquiryTransactionStatus")
    @ResponsePayload
    public JAXBElement<InquiryTransactionStatusResponse> inquiryTransactionStatus(@RequestPayload InquiryTransactionStatus request) throws Exception {
        BPayPayment payment = paymentRepository.findByCorrelationId(request.getArg0().getCorrelationId());
        ResponseInquiryTransactionStatusVO responseData = new ResponseInquiryTransactionStatusVO();
        responseData.setEsito(generateEsito(EsitoEnum.fromCode(payment.getOutcome())));
        InquiryTransactionStatusResponse response = new InquiryTransactionStatusResponse();
        response.setReturn(responseData);
        return factory.createInquiryTransactionStatusResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inserimentoRichiestaPagamentoPagoPa")
    @ResponsePayload
    public JAXBElement<InserimentoRichiestaPagamentoPagoPaResponse> inserimentoRichiestaPagamentoPagoPa(@RequestPayload InserimentoRichiestaPagamentoPagoPa request) throws InterruptedException {
        RichiestaPagamentoPagoPaVO requestData = request.getArg0().getRichiestaPagamentoPagoPa();
        BPayPayment payment = new BPayPayment();
        payment.setIdPagoPa(requestData.getIdPagoPa());
        payment.setAmount(requestData.getImporto());
        payment.setOutcome(EsitoEnum.OK.getCodice());
        payment.setIdPsp(requestData.getIdPSP());
        payment.setCorrelationId(new UUID(10, 10).toString());
        paymentRepository.save(payment);
        ResponseInserimentoRichiestaPagamentoPagoPaVO responseData = new ResponseInserimentoRichiestaPagamentoPagoPaVO();
        responseData.setEsito(generateEsito(EsitoEnum.OK));
        responseData.setCorrelationId("bf6b1ac5-932f-485c-84c8-ecef7ac26461");
        InserimentoRichiestaPagamentoPagoPaResponse response = new InserimentoRichiestaPagamentoPagoPaResponse();
        response.setReturn(responseData);
        callbackPm(payment);
        return factory.createInserimentoRichiestaPagamentoPagoPaResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "stornoPagamento")
    @ResponsePayload
    public JAXBElement<StornoPagamentoResponse> refundRequest(@RequestPayload StornoPagamento request) throws InterruptedException {
        BPayPayment payment = paymentRepository.findByCorrelationId(request.getArg0().getEndToEndId());
        payment.setRefunded(true);
        paymentRepository.save(payment);
        ResponseStornoPagamentoVO responseData = new ResponseStornoPagamentoVO();
        responseData.setEsito(generateEsito(EsitoEnum.OK));
        StornoPagamentoResponse response = new StornoPagamentoResponse();
        response.setReturn(responseData);
        callbackPm(payment);
        return factory.createStornoPagamentoResponse(response);
    }

    @Async
    private void callbackPm(BPayPayment payment) throws InterruptedException {
        Thread.sleep(10000);
        TransactionUpdateRequest request = new TransactionUpdateRequest(payment.getCorrelationId());
        pmClient.updateTransaction(payment.getIdPagoPa(), request);
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