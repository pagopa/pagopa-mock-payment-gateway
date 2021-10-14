package it.gov.pagopa.paypalpsp.mockcontroller;


import it.gov.pagopa.db.entity.*;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.db.repository.TablePaymentPayPalRepository;
import it.gov.pagopa.db.repository.TablePpOnboardingBackRepository;
import it.gov.pagopa.db.repository.TablePpPaypalManagementRepository;
import it.gov.pagopa.db.repository.TableUserPayPalRepository;
import it.gov.pagopa.paypalpsp.dto.*;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpEsitoResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import it.gov.pagopa.paypalpsp.util.PayPalCreateResponse;
import it.gov.pagopa.paypalpsp.util.PaypalUtils;
import it.gov.pagopa.util.UrlUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Validated
@RestController
@RequestMapping("/paypalpsp")
@Log4j2
public class PayPalPspRestController {

    private static final int TIMEOUT = 20000;

    @Autowired
    private TablePpPaypalManagementRepository managementRepository;

    @Autowired
    private TablePpOnboardingBackRepository tablePpOnboardingBackRepository;

    @Autowired
    private TableUserPayPalRepository tableUserPayPalRepository;

    @Autowired
    private TablePaymentPayPalRepository tablePaymentPayPalRepository;

    @Autowired
    private PaypalUtils paypalUtils;

    @Value("${server.public-url}")
    private String publicUrl;

    @PostMapping("/api/pp_onboarding_back")
    @Transactional
    public ResponseEntity<PpOnboardingBackResponse> homePage(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                             @Valid @RequestBody PpOnboardingBackRequest ppOnboardingBackRequest) throws URISyntaxException, InterruptedException, TimeoutException {
        TableClient tableClient = paypalUtils.getClientAuthenticated(authorization);
        if (tableClient == null) {
            log.error("Invalid authorization: " + authorization);
            return PayPalCreateResponse.createResponseErrorOnboarding(PpResponseErrCode.AUTORIZZAZIONE_NEGATA);
        }

        String idAppIo = ppOnboardingBackRequest.getIdAppIo();
        TablePpPaypalManagement onboardingBackManagement = managementRepository.findByIdAppIoAndApiIdAndClient(idAppIo, ApiPaypalIdEnum.ONBOARDING, tableClient);

        //Manage error defined by user
        if (isTimeout(onboardingBackManagement)) {
            log.info("Going in timeout for idAppIo: " + ppOnboardingBackRequest.getIdAppIo());
            Thread.sleep(TIMEOUT);
            throw new TimeoutException("Simulate PayPal psp Timeout");
        } else if (isCustomError(onboardingBackManagement)) {
            return PayPalCreateResponse.createResponseErrorOnboarding(PpResponseErrCode.of(onboardingBackManagement.getErrCodeValue()));
        }

        //manage error code 19
        TableUserPayPal byIdAppIoAndDeletedFalse = tableUserPayPalRepository.findByIdAppIoAndClientAndDeletedFalse(idAppIo, tableClient);
        if (byIdAppIoAndDeletedFalse != null) {
            return PayPalCreateResponse.manageErrorResponseAlreadyOnboarded(byIdAppIoAndDeletedFalse);
        }

        String idBack = UUID.randomUUID().toString();
        saveAndUpdateTable(ppOnboardingBackRequest, idBack, tableClient);
        String returnUrl = UrlUtils.normalizeUrl(publicUrl + "/paypalweb/pp_onboarding_call");
        PpOnboardingBackResponse ppOnboardingBackResponse = new PpOnboardingBackResponse();
        ppOnboardingBackResponse.setEsito(PpEsitoResponseCode.OK);
        ppOnboardingBackResponse.setUrlToCall(UrlUtils.addQueryParams(returnUrl, "id_back", idBack));
        return ResponseEntity.ok(ppOnboardingBackResponse);
    }

    @Transactional
    @PostMapping("/api/pp_pay_direct")
    public ResponseEntity<PpPayDirectResponse> directPayment(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                             @Valid @RequestBody PpPayDirectRequest ppPayDirectRequest) throws InterruptedException, TimeoutException {
        String idAppIo = ppPayDirectRequest.getIdAppIo();

        TableClient tableClient = paypalUtils.getClientAuthenticated(authorization);
        if (tableClient == null) {
            log.error("Invalid authorization: " + authorization);
            return PayPalCreateResponse.createResponseErrorPayment(PpResponseErrCode.AUTORIZZAZIONE_NEGATA);
        }

        ResponseEntity<PpPayDirectResponse> ppPayDirectResponse = null;
        TableUserPayPal byIdAppIoAndDeletedFalse = tableUserPayPalRepository.findByIdAppIoAndClientAndDeletedFalse(idAppIo, tableClient);
        TablePpPaypalManagement onboardingBackManagement = managementRepository.findByIdAppIoAndApiIdAndClient(idAppIo, ApiPaypalIdEnum.PAYMENT, tableClient);
        try {
            if (byIdAppIoAndDeletedFalse == null) {
                ppPayDirectResponse = PayPalCreateResponse.createResponseErrorPayment(PpResponseErrCode.PAYMENT_ID_APP_IO_NON_ESISTE);
            } else if (isTimeout(onboardingBackManagement)) {
                log.info("Going in timeout for idAppIo " + idAppIo);
                ppPayDirectResponse = PayPalCreateResponse.createResponseErrorPayment(PpResponseErrCode.TIMEOUT);
                Thread.sleep(TIMEOUT);
                throw new TimeoutException();
            } else if (isCustomError(onboardingBackManagement)) {
                ppPayDirectResponse = PayPalCreateResponse.createResponseErrorPayment(PpResponseErrCode.of(onboardingBackManagement.getErrCodeValue()));
            } else {
                ppPayDirectResponse = PayPalCreateResponse.createSuccessPaymentResponse(ppPayDirectRequest);
            }
            return ppPayDirectResponse;
        } finally {
            if (ppPayDirectResponse != null && ppPayDirectResponse.getBody() != null) {
                savePayment(ppPayDirectRequest, ppPayDirectResponse.getBody(), byIdAppIoAndDeletedFalse);
            }
        }
    }

    @Transactional
    @PostMapping("/api/pp_refund_direct")
    public ResponseEntity<PpRefundDirectResponse> refund(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                         @Valid @RequestBody PpRefundDirectRequest ppPayDirectRequest) throws InterruptedException, TimeoutException {

        ResponseEntity<PpRefundDirectResponse> response = null;
        String idTrsAppIo = ppPayDirectRequest.getIdTrsAppIo();
        TablePaymentPayPal tablePaymentPayPal = null;

        try {
            TableClient tableClient = paypalUtils.getClientAuthenticated(authorization);
            if (tableClient == null) {
                log.error("Invalid authorization: " + authorization);
                return PayPalCreateResponse.createRefundResponseError(PpResponseErrCode.AUTORIZZAZIONE_NEGATA);
            }
            tablePaymentPayPal = tablePaymentPayPalRepository.findByIdTrsAppIoAndTableUserPayPal_client(idTrsAppIo, tableClient);

            if (tablePaymentPayPal == null) {
                log.error("Payment not found for idTrsAppIo: " + idTrsAppIo);
                return PayPalCreateResponse.createRefundResponseError(PpResponseErrCode.ID_TRS_NON_VALIDO);
            } else if (!checkRequestAndDataOnDatabase(ppPayDirectRequest, tablePaymentPayPal)) {
                return PayPalCreateResponse.createRefundResponseError(PpResponseErrCode.ID_TRS_OR_IMPORT_NOT_MATCH);
            }
            String idAppIo = tablePaymentPayPal.getTableUserPayPal().getIdAppIo();
            TablePpPaypalManagement onboardingBackManagement = managementRepository.findByIdAppIoAndApiIdAndClient(idAppIo, ApiPaypalIdEnum.REFUND, tableClient);

            if (isTimeout(onboardingBackManagement)) {
                log.info("Going in timeout for idAppIo " + idAppIo);
                response = PayPalCreateResponse.createRefundResponseError(PpResponseErrCode.TIMEOUT);
                Thread.sleep(TIMEOUT);
                throw new TimeoutException();
            } else if (isCustomError(onboardingBackManagement)) {
                response = PayPalCreateResponse.createRefundResponseError(PpResponseErrCode.of(onboardingBackManagement.getErrCodeValue()));
            } else {
                response = PayPalCreateResponse.createSuccessRefundResponse();
            }
            return response;
        } finally {
            if (response != null) {
                PpRefundDirectResponse responseBody = response.getBody();
                if (responseBody != null) {
                    updatePayment(responseBody, tablePaymentPayPal);
                }
            }
        }
    }

    private boolean isCustomError(TablePpPaypalManagement onboardingBackManagement) {
        return onboardingBackManagement != null && StringUtils.isNotBlank(onboardingBackManagement.getErrCodeValue());
    }

    private boolean isTimeout(TablePpPaypalManagement onboardingBackManagement) {
        return onboardingBackManagement != null && StringUtils.equals(onboardingBackManagement.getErrCodeValue(), PpResponseErrCode.TIMEOUT.getCode());
    }

    private boolean checkRequestAndDataOnDatabase(PpRefundDirectRequest ppPayDirectRequest, TablePaymentPayPal tablePaymentPayPal) {
        String idAppIo = tablePaymentPayPal.getTableUserPayPal().getIdAppIo();
        BigDecimal importo = tablePaymentPayPal.getImporto();
        return StringUtils.equals(idAppIo, ppPayDirectRequest.getIdAppIo()) && importo.equals(ppPayDirectRequest.getImporto());
    }

    private void updatePayment(PpRefundDirectResponse body, TablePaymentPayPal tablePaymentPayPal) {
        tablePaymentPayPal.setEsitoRefund(body.getEsito());
        tablePaymentPayPal.setErrCodeRefund(body.getErrCod());
        tablePaymentPayPalRepository.save(tablePaymentPayPal);
    }

    private void savePayment(PpPayDirectRequest ppPayDirectRequest, PpPayDirectResponse ppPayDirectResponse, TableUserPayPal byIdAppIoAndDeletedFalse) {
        TablePaymentPayPal tablePaymentPayPal = TablePaymentPayPal.builder()
                .errCode(ppPayDirectResponse.getErrCod())
                .esito(ppPayDirectResponse.getEsito())
                .fee(ppPayDirectRequest.getFee())
                .idTrsAppIo(ppPayDirectRequest.getIdTrsAppIo())
                .importo(ppPayDirectRequest.getImporto())
                .tableUserPayPal(byIdAppIoAndDeletedFalse)
                .idTrsPaypal(ppPayDirectResponse.getIdTrsPaypal())
                .build();
        tablePaymentPayPalRepository.save(tablePaymentPayPal);
    }

    private void saveAndUpdateTable(PpOnboardingBackRequest ppOnboardingBackRequest, String idBack, TableClient tableClient) {
        String idAppIo = ppOnboardingBackRequest.getIdAppIo();
        tablePpOnboardingBackRepository.setUsedTrueByIdBack(idAppIo, tableClient);
        TablePpOnboardingBack tablePpOnboardingBack = new TablePpOnboardingBack();
        tablePpOnboardingBack.setIdAppIo(idAppIo);
        tablePpOnboardingBack.setTimestamp(Instant.now());
        tablePpOnboardingBack.setUrlReturn(ppOnboardingBackRequest.getUrlReturn());
        tablePpOnboardingBack.setIdBack(idBack);
        tablePpOnboardingBack.setClient(tableClient);
        tablePpOnboardingBackRepository.save(tablePpOnboardingBack);
    }
}