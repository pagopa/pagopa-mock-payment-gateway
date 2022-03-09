package it.gov.pagopa.bpay.controller;

import it.gft.p2b.srv.pp.*;
import it.gov.pagopa.bpay.client.*;
import it.gov.pagopa.bpay.dto.*;
import it.gov.pagopa.bpay.entity.*;
import it.gov.pagopa.bpay.repository.*;
import it.gov.pagopa.db.repository.*;
import lombok.extern.log4j.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.ws.server.endpoint.annotation.*;

import javax.xml.bind.*;
import java.util.*;

@Endpoint
@Log4j2
public class BPayController {

    @Autowired
    private TableConfigRepository configRepository;

    @Autowired
    private BPayPaymentRepository paymentRepository;

    @Autowired
    private PmClientImpl pmClient;

    private String outcomeConfig;

    private String currentClient;

    private static final String NAMESPACE_URI = "http://p2b.gft.it/srv/pp";

    private static final ObjectFactory factory = new ObjectFactory();

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inquiryTransactionStatus")
    @ResponsePayload
    public JAXBElement<InquiryTransactionStatusResponse> inquiryTransactionStatus(@RequestPayload InquiryTransactionStatus request) {
        refreshConfigs();
        RequestInquiryTransactionStatusVO requestData = request.getArg0();
        BPayPayment payment = findPayment(requestData.getIdPagoPa(), requestData.getCorrelationId());
        ResponseInquiryTransactionStatusVO responseData = new ResponseInquiryTransactionStatusVO();
        responseData.setEsito(generateEsito(EsitoEnum.fromCode(payment == null ? EsitoEnum.PAYMENT_NOT_FOUND.getCodice() : payment.getOutcome())));
        InquiryTransactionStatusResponse response = new InquiryTransactionStatusResponse();
        response.setReturn(responseData);
        return factory.createInquiryTransactionStatusResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inserimentoRichiestaPagamentoPagoPa")
    @ResponsePayload
    public JAXBElement<InserimentoRichiestaPagamentoPagoPaResponse> inserimentoRichiestaPagamentoPagoPa(@RequestPayload InserimentoRichiestaPagamentoPagoPa request) {
        refreshConfigs();
        RichiestaPagamentoPagoPaVO requestData = request.getArg0().getRichiestaPagamentoPagoPa();
        BPayPayment payment = new BPayPayment();
        payment.setIdPagoPa(requestData.getIdPagoPa());
        payment.setAmount(requestData.getImporto());
        payment.setOutcome(outcomeConfig);
        payment.setIdPsp(requestData.getIdPSP());
        String correlationId = UUID.randomUUID().toString();
        payment.setCorrelationId(correlationId);
        payment.setClientHostname(currentClient);
        paymentRepository.save(payment);
        ResponseInserimentoRichiestaPagamentoPagoPaVO responseData = new ResponseInserimentoRichiestaPagamentoPagoPaVO();
        responseData.setEsito(generateEsito(EsitoEnum.fromCode(outcomeConfig)));
        responseData.setCorrelationId(correlationId);
        InserimentoRichiestaPagamentoPagoPaResponse response = new InserimentoRichiestaPagamentoPagoPaResponse();
        response.setReturn(responseData);
        pmClient.callbackPm(payment);
        return factory.createInserimentoRichiestaPagamentoPagoPaResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "stornoPagamento")
    @ResponsePayload
    public JAXBElement<StornoPagamentoResponse> refundRequest(@RequestPayload StornoPagamento request) {
        refreshConfigs();
        RequestStornoPagamentoVO requestData = request.getArg0();
        BPayPayment payment = findPayment(requestData.getIdPagoPa(), requestData.getEndToEndId());
        payment.setRefundOutcome(outcomeConfig);
        paymentRepository.save(payment);
        ResponseStornoPagamentoVO responseData = new ResponseStornoPagamentoVO();
        responseData.setEsito(generateEsito(EsitoEnum.fromCode(outcomeConfig)));
        StornoPagamentoResponse response = new StornoPagamentoResponse();
        response.setReturn(responseData);
        pmClient.callbackPm(payment);
        return factory.createStornoPagamentoResponse(response);
    }

    private void refreshConfigs() {
        currentClient = configRepository.findByPropertyKey("BPAY_CURRENT_CLIENT").getPropertyValue();
        outcomeConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_OUTCOME").getPropertyValue();
        if (BooleanUtils.toBoolean(configRepository.findByPropertyKey("BPAY_PAYMENT_TIMEOUT").getPropertyValue())) {
            throw new RuntimeException("Timeout");
        }
    }

    private BPayPayment findPayment(String idPagoPa, String correlationId) {
        return StringUtils.isNotBlank(idPagoPa) ? paymentRepository.findByIdPagoPa(idPagoPa) : paymentRepository.findByCorrelationId(correlationId);
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