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

import javax.annotation.*;
import javax.xml.bind.*;
import java.math.BigDecimal;
import java.util.*;

@Endpoint
@Log4j2
public class BPayController {

    @Value("${bpay.payment.x-correlation-id}")
    private String xCorrelationId;

    @Value("${bpay.payment.amount}")
    private String amount;

    @Value("${MOCK_PROFILE}")
    private String profile;

    @Autowired
    private TableClientRepository clientRepository;

    @Autowired
    private TableConfigRepository configRepository;

    @Autowired
    private BPayPaymentRepository paymentRepository;

    private String clientBaseUrl;

    @Autowired
    private PmClientImpl pmClient;

    @PostConstruct
    protected void init() {
        clientBaseUrl = clientRepository.findByClientName(profile).getBaseUrl();
    }

    private String paymentOutcomeConfig;

    private String refundOutcomeConfig;

    private String inquiryOutcomeConfig;

    private static final String NAMESPACE_URI = "http://p2b.gft.it/srv/pp";

    private static final ObjectFactory factory = new ObjectFactory();

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inquiryTransactionStatus")
    @ResponsePayload
    public JAXBElement<InquiryTransactionStatusResponse> inquiryTransactionStatus(@RequestPayload InquiryTransactionStatus request) {
        refreshConfigs();
        RequestInquiryTransactionStatusVO requestData = request.getArg0();
        BPayPayment payment = findPayment(requestData.getIdPagoPa(), requestData.getCorrelationId());
        ResponseInquiryTransactionStatusVO responseData = new ResponseInquiryTransactionStatusVO();
        EsitoEnum esito = EsitoEnum.fromCode(payment == null ? EsitoEnum.PAYMENT_NOT_FOUND.getCodice() : inquiryOutcomeConfig);
        responseData.setEsito(generateEsito(esito));
        responseData.setEsitoPagamento(esito.isEsito() ? "EFF" : "ERR");
        InquiryTransactionStatusResponse response = new InquiryTransactionStatusResponse();
        response.setReturn(responseData);
        return factory.createInquiryTransactionStatusResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "inserimentoRichiestaPagamentoPagoPa")
    @ResponsePayload
    public JAXBElement<InserimentoRichiestaPagamentoPagoPaResponse> inserimentoRichiestaPagamentoPagoPa(@RequestPayload InserimentoRichiestaPagamentoPagoPa request) {
        refreshConfigs();
        loadXCorrelationIdAmount();
        RichiestaPagamentoPagoPaVO requestData = request.getArg0().getRichiestaPagamentoPagoPa();
        BPayPayment payment = new BPayPayment();
        payment.setIdPagoPa(requestData.getIdPagoPa());
        payment.setAmount(requestData.getImporto());
        payment.setOutcome(paymentOutcomeConfig);
        payment.setIdPsp(requestData.getIdPSP());
        String correlationId = Objects.nonNull(amount) && requestData.getImporto().equals(amount) ? xCorrelationId : UUID.randomUUID().toString();
        payment.setCorrelationId(correlationId);
        paymentRepository.save(payment);
        ResponseInserimentoRichiestaPagamentoPagoPaVO responseData = new ResponseInserimentoRichiestaPagamentoPagoPaVO();
        responseData.setEsito(generateEsito(EsitoEnum.fromCode(paymentOutcomeConfig)));
        responseData.setCorrelationId(correlationId);
        InserimentoRichiestaPagamentoPagoPaResponse response = new InserimentoRichiestaPagamentoPagoPaResponse();
        response.setReturn(responseData);
        pmClient.callbackPm(payment, clientBaseUrl);
        return factory.createInserimentoRichiestaPagamentoPagoPaResponse(response);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "stornoPagamento")
    @ResponsePayload
    public JAXBElement<StornoPagamentoResponse> refundRequest(@RequestPayload StornoPagamento request) {
        refreshConfigs();
        RequestStornoPagamentoVO requestData = request.getArg0();
        BPayPayment payment = findPayment(requestData.getIdPagoPa(), requestData.getEndToEndId());
        payment.setRefundOutcome(refundOutcomeConfig);
        paymentRepository.save(payment);
        ResponseStornoPagamentoVO responseData = new ResponseStornoPagamentoVO();
        responseData.setEsito(generateEsito(EsitoEnum.fromCode(refundOutcomeConfig)));
        StornoPagamentoResponse response = new StornoPagamentoResponse();
        response.setReturn(responseData);
        pmClient.callbackPm(payment, clientBaseUrl);
        return factory.createStornoPagamentoResponse(response);
    }

    private void refreshConfigs() {
        paymentOutcomeConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_OUTCOME").getPropertyValue();
        refundOutcomeConfig = configRepository.findByPropertyKey("BPAY_REFUND_OUTCOME").getPropertyValue();
        inquiryOutcomeConfig = configRepository.findByPropertyKey("BPAY_INQUIRY_OUTCOME").getPropertyValue();
        try {
            Thread.sleep(Integer.parseInt(configRepository.findByPropertyKey("BPAY_PAYMENT_TIMEOUT_MS").getPropertyValue()));
        } catch (InterruptedException e) {
            log.warn(e);
        }
    }

    private BigDecimal loadXCorrelationIdAmount() {
        return Optional.ofNullable(amount).map(Double::valueOf).map(BigDecimal::valueOf).orElse(null);
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