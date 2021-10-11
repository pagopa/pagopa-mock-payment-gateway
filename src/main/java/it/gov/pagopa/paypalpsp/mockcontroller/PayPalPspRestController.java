package it.gov.pagopa.paypalpsp.mockcontroller;


import it.gov.pagopa.db.entity.*;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.db.repository.*;
import it.gov.pagopa.paypalpsp.PaypalUtils;
import it.gov.pagopa.paypalpsp.dto.*;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpEsitoResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Validated
@RestController
@RequestMapping("/paypalpsp")
@Log4j2
public class PayPalPspRestController {

    @Autowired
    private TablePpPaypalManagementRepository onboardingBackManagementRepository;

    @Autowired
    private TablePpOnboardingBackRepository tablePpOnboardingBackRepository;

    @Autowired
    private TableUserPayPalRepository tableUserPayPalRepository;

    @Autowired
    private TablePaymentPayPalRepository tablePaymentPayPalRepository;

    @Autowired
    private TableClientRepository tableClientRepository;

    @Autowired
    private PaypalUtils paypalUtils;

    @Value("${server.public-url}")
    private String publicUrl;

    private static final String BEARER_REGEX = "Bearer\\s.{3,}";

    @PostMapping("/api/pp_onboarding_back")
    @Transactional
    public ResponseEntity<PpOnboardingBackResponse> homePage(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                             @Valid @RequestBody PpOnboardingBackRequest ppOnboardingBackRequest) throws URISyntaxException, InterruptedException, TimeoutException {
        TableClient tableClient;
        if (StringUtils.isBlank(authorization) || !authorization.matches(BEARER_REGEX) || (tableClient = tableClientRepository.findByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "))) == null) {
            log.error("Invalid authorization: " + authorization);
            return createResponseErrorOnboarding(PpResponseErrCode.AUTORIZZAZIONE_NEGATA);
        }

        String idAppIo = ppOnboardingBackRequest.getIdAppIo();
        TablePpPaypalManagement onboardingBackManagement = onboardingBackManagementRepository.findByIdAppIoAndApiIdAndClient(idAppIo, ApiPaypalIdEnum.ONBOARDING, tableClient);

        //Manage error defined by user
        if (onboardingBackManagement != null
                && StringUtils.equals(onboardingBackManagement.getErrCodeValue(), PpResponseErrCode.TIMEOUT.getCode())) {
            log.info("Going in timeout for idAppIo: " + ppOnboardingBackRequest.getIdAppIo());
            Thread.sleep(20000);
            throw new TimeoutException("Simulate PayPal psp Timeout");
        } else if (onboardingBackManagement != null && StringUtils.isNotBlank(onboardingBackManagement.getErrCodeValue())) {
            return createResponseErrorOnboarding(PpResponseErrCode.of(onboardingBackManagement.getErrCodeValue()));
        }

        //manage error code 19
        TableUserPayPal byIdAppIoAndDeletedFalse = tableUserPayPalRepository.findByIdAppIoAndClientAndDeletedFalse(idAppIo, tableClient);
        if (byIdAppIoAndDeletedFalse != null) {
            return manageErrorResponseAlreadyOnboarded(byIdAppIoAndDeletedFalse);
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

        TableClient tableClient;
        if (StringUtils.isBlank(authorization) || !authorization.matches(BEARER_REGEX) || (tableClient = tableClientRepository.findByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "))) == null) {
            log.error("Invalid authorization: " + authorization);
            return createResponseErrorPayment(PpResponseErrCode.AUTORIZZAZIONE_NEGATA);
        }

        ResponseEntity<PpPayDirectResponse> ppPayDirectResponse = null;
        TableUserPayPal byIdAppIoAndDeletedFalse = tableUserPayPalRepository.findByIdAppIoAndClientAndDeletedFalse(idAppIo, tableClient);
        TablePpPaypalManagement onboardingBackManagement = onboardingBackManagementRepository.findByIdAppIoAndApiIdAndClient(idAppIo, ApiPaypalIdEnum.PAYMENT, tableClient);
        try {
            if (byIdAppIoAndDeletedFalse == null) {
                ppPayDirectResponse = createResponseErrorPayment(PpResponseErrCode.PAYMENT_ID_APP_IO_NON_ESISTE);
            } else if (onboardingBackManagement != null
                    && StringUtils.equals(onboardingBackManagement.getErrCodeValue(), PpResponseErrCode.TIMEOUT.getCode())) {
                log.info("Going in timeout for idAppIo " + idAppIo);
                ppPayDirectResponse = createResponseErrorPayment(PpResponseErrCode.TIMEOUT);
                Thread.sleep(20000);
                throw new TimeoutException();
            } else if (onboardingBackManagement != null && StringUtils.isNotBlank(onboardingBackManagement.getErrCodeValue())) {
                ppPayDirectResponse = createResponseErrorPayment(PpResponseErrCode.of(onboardingBackManagement.getErrCodeValue()));
            } else {
                ppPayDirectResponse = createSuccessPaymentResponse(ppPayDirectRequest);
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
            TableClient tableClient;
            if (StringUtils.isBlank(authorization) || !authorization.matches(BEARER_REGEX) || (tableClient = tableClientRepository.findByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "))) == null) {
                log.error("Invalid authorization: " + authorization);
                return createRefundResponseError(PpResponseErrCode.AUTORIZZAZIONE_NEGATA);
            }
            tablePaymentPayPal = tablePaymentPayPalRepository.findByIdTrsAppIoAndTableUserPayPal_client(idTrsAppIo, tableClient);

            if (tablePaymentPayPal == null) {
                log.error("Payment not found for idTrsAppIo: " + idTrsAppIo);
                return createRefundResponseError(PpResponseErrCode.ID_TRS_NON_VALIDO);
            } else if (!checkRequestAndDataOnDatabase(ppPayDirectRequest, tablePaymentPayPal)) {
                return createRefundResponseError(PpResponseErrCode.ID_TRS_OR_IMPORT_NOT_MATCH);
            }
            String idAppIo = tablePaymentPayPal.getTableUserPayPal().getIdAppIo();
            TablePpPaypalManagement onboardingBackManagement = onboardingBackManagementRepository.findByIdAppIoAndApiIdAndClient(idAppIo, ApiPaypalIdEnum.REFUND, tableClient);

            if (onboardingBackManagement != null
                    && StringUtils.equals(onboardingBackManagement.getErrCodeValue(), PpResponseErrCode.TIMEOUT.getCode())) {
                log.info("Going in timeout for idAppIo " + idAppIo);
                response = createRefundResponseError(PpResponseErrCode.TIMEOUT);
                Thread.sleep(20000);
                throw new TimeoutException();
            } else if (onboardingBackManagement != null && StringUtils.isNotBlank(onboardingBackManagement.getErrCodeValue())) {
                response = createRefundResponseError(PpResponseErrCode.of(onboardingBackManagement.getErrCodeValue()));
            } else {
                response = createSuccessRefundResponse();
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

    private ResponseEntity<PpPayDirectResponse> createSuccessPaymentResponse(PpPayDirectRequest ppPayDirectRequest) {

        PpPayDirectResponse response = new PpPayDirectResponse();
        response.setEsito(PpEsitoResponseCode.OK);
        response.setIdTrsPaypal(StringUtils.leftPad(ppPayDirectRequest.getIdTrsAppIo(), 20, "0"));
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<PpRefundDirectResponse> createSuccessRefundResponse() {

        PpRefundDirectResponse response = new PpRefundDirectResponse();
        response.setEsito(PpEsitoResponseCode.OK);
        return ResponseEntity.ok(response);
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

    private ResponseEntity<PpOnboardingBackResponse> manageErrorResponseAlreadyOnboarded(TableUserPayPal tableUserPayPal) {
        PpResponseErrCode codiceContrattoPresente = PpResponseErrCode.CODICE_CONTRATTO_PRESENTE;
        PpDefaultErrorResponse onboardingBackResponseResponseEntity = manageErrorResponse(codiceContrattoPresente);
        PpDefaultErrorResponse ppOnboardingBackResponse = Objects.requireNonNull(onboardingBackResponseResponseEntity);
        PpOnboardingBackResponse ppOnboardingBackResponse1 = new PpOnboardingBackResponse();
        ppOnboardingBackResponse1.setEmailPp(paypalUtils.obfuscateEmail(tableUserPayPal.getPaypalEmail()));
        ppOnboardingBackResponse1.setIdPp(tableUserPayPal.getPaypalId());
        ppOnboardingBackResponse1.setPpDefaultErrorResponse(ppOnboardingBackResponse);
        return ResponseEntity.status(codiceContrattoPresente.getHttpStatus()).body(ppOnboardingBackResponse1);
    }

    private PpDefaultErrorResponse manageErrorResponse(PpResponseErrCode errCode) {
        PpDefaultErrorResponse build = new PpOnboardingBackResponse();
        build.setEsito(PpEsitoResponseCode.KO);
        build.setErrCod(errCode);
        build.setErrDesc(errCode.getDescription());
        return build;
    }

    private ResponseEntity<PpOnboardingBackResponse> createResponseErrorOnboarding(PpResponseErrCode ppResponseErrCode) {
        PpDefaultErrorResponse ppDefaultErrorResponse = manageErrorResponse(ppResponseErrCode);
        return ResponseEntity.status(ppResponseErrCode.getHttpStatus()).body(new PpOnboardingBackResponse(ppDefaultErrorResponse));
    }

    private ResponseEntity<PpPayDirectResponse> createResponseErrorPayment(PpResponseErrCode ppResponseErrCode) {
        PpDefaultErrorResponse ppDefaultErrorResponse = manageErrorResponse(ppResponseErrCode);
        return ResponseEntity.status(ppResponseErrCode.getHttpStatus()).body(new PpPayDirectResponse(ppDefaultErrorResponse));
    }

    private ResponseEntity<PpRefundDirectResponse> createRefundResponseError(PpResponseErrCode ppResponseErrCode) {
        PpDefaultErrorResponse ppDefaultErrorResponse = manageErrorResponse(ppResponseErrCode);
        return ResponseEntity.status(ppResponseErrCode.getHttpStatus()).body(new PpRefundDirectResponse(ppDefaultErrorResponse));
    }
}