package it.gov.pagopa.paypalpsp.mockcontroller;


import it.gov.pagopa.db.entity.TablePaymentPayPal;
import it.gov.pagopa.db.entity.TablePpOnboardingBack;
import it.gov.pagopa.db.entity.TablePpPaypalManagement;
import it.gov.pagopa.db.entity.TableUserPayPal;
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
        if (StringUtils.isBlank(authorization) || !authorization.matches(BEARER_REGEX) || !tableClientRepository.existsByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "))) {
            log.error("Invalid authorization: " + authorization);
            return createResponseErrorOnboarding(PpResponseErrCode.AUTORIZZAZIONE_NEGATA);
        }

        String idAppIo = ppOnboardingBackRequest.getIdAppIo();
        TablePpPaypalManagement onboardingBackManagement = onboardingBackManagementRepository.findByIdAppIoAndApiId(idAppIo, ApiPaypalIdEnum.ONBOARDING);

        //Manage error defined by user
        if (onboardingBackManagement != null
                && StringUtils.equals(onboardingBackManagement.getErrCodeValue(), PpResponseErrCode.TIMEOUT.getCode())) {
            log.info("Going in timeout: " + ppOnboardingBackRequest.getIdAppIo());
            Thread.sleep(20000);
            throw new TimeoutException();
        } else if (onboardingBackManagement != null && StringUtils.isNotBlank(onboardingBackManagement.getErrCodeValue())) {
            return createResponseErrorOnboarding(PpResponseErrCode.of(onboardingBackManagement.getErrCodeValue()));
        }

        //manage error code 19
        TableUserPayPal byIdAppIoAndDeletedFalse = tableUserPayPalRepository.findByIdAppIoAndDeletedFalse(idAppIo);
        if (byIdAppIoAndDeletedFalse != null) {
            return manageErrorResponseAlreadyOnboarded(byIdAppIoAndDeletedFalse);
        }

        String idBack = UUID.randomUUID().toString();
        saveAndUpdateTable(ppOnboardingBackRequest, idBack);
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

        if (StringUtils.isBlank(authorization) || !authorization.matches(BEARER_REGEX) || !tableClientRepository.existsByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "))) {
            log.error("Invalid authorization: " + authorization);
            return createResponseErrorPayment(PpResponseErrCode.AUTORIZZAZIONE_NEGATA);
        }

        ResponseEntity<PpPayDirectResponse> ppPayDirectResponse = null;
        TableUserPayPal byIdAppIoAndDeletedFalse = tableUserPayPalRepository.findByIdAppIoAndDeletedFalse(idAppIo);
        TablePpPaypalManagement onboardingBackManagement = onboardingBackManagementRepository.findByIdAppIoAndApiId(idAppIo, ApiPaypalIdEnum.PAYMENT);
        try {
            if (byIdAppIoAndDeletedFalse == null) {
                ppPayDirectResponse = createResponseErrorPayment(PpResponseErrCode.ID_APP_IO_NON_ESISTE);
            } else if (onboardingBackManagement != null
                    && StringUtils.equals(onboardingBackManagement.getErrCodeValue(), PpResponseErrCode.TIMEOUT.getCode())) {
                log.info("Going in timeout: " + idAppIo);
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
        TablePaymentPayPal tablePaymentPayPal = tablePaymentPayPalRepository.findByIdTrsAppIo(idTrsAppIo);

        try {
            if (StringUtils.isBlank(authorization) || !authorization.matches(BEARER_REGEX) || !tableClientRepository.existsByAuthKeyAndDeletedFalse(StringUtils.remove(authorization, "Bearer "))) {
                log.error("Invalid authorization: " + authorization);
                return createRefundResponseError(PpResponseErrCode.AUTORIZZAZIONE_NEGATA);
            }

            if(tablePaymentPayPal == null){
                log.error("Payment not found for idTrsAppIo: " + idTrsAppIo);
                return createRefundResponseError(PpResponseErrCode.ID_APP_IO_NON_ESISTE);
            }
            String idAppIo = tablePaymentPayPal.getTableUserPayPal().getIdAppIo();
            TablePpPaypalManagement onboardingBackManagement = onboardingBackManagementRepository.findByIdAppIoAndApiId(idAppIo, ApiPaypalIdEnum.REFUND);

            if (onboardingBackManagement != null
                    && StringUtils.equals(onboardingBackManagement.getErrCodeValue(), PpResponseErrCode.TIMEOUT.getCode())) {
                log.info("Going in timeout: " + idAppIo);
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

        PpPayDirectResponse build = PpPayDirectResponse.builder().esito(PpEsitoResponseCode.OK)
                .idTrsPaypal(StringUtils.leftPad(ppPayDirectRequest.getIdTrsAppIo(), 20, "0"))
                .build();
        return ResponseEntity.ok(build);
    }

    private ResponseEntity<PpRefundDirectResponse> createSuccessRefundResponse() {

        PpRefundDirectResponse build = PpRefundDirectResponse.builder().esito(PpEsitoResponseCode.OK)
                .build();
        return ResponseEntity.ok(build);
    }

    private void saveAndUpdateTable(PpOnboardingBackRequest ppOnboardingBackRequest, String idBack) {
        String idAppIo = ppOnboardingBackRequest.getIdAppIo();
        tablePpOnboardingBackRepository.setUsedTrueByIdBack(idAppIo);
        TablePpOnboardingBack tablePpOnboardingBack = new TablePpOnboardingBack();
        tablePpOnboardingBack.setIdAppIo(idAppIo);
        tablePpOnboardingBack.setTimestamp(Instant.now());
        tablePpOnboardingBack.setUrlReturn(ppOnboardingBackRequest.getUrlReturn());
        tablePpOnboardingBack.setIdBack(idBack);
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
        build.setErrDesc(errCode.name());
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