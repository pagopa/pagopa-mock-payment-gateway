package it.gov.pagopa.postepay.controller;

import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.postepay.dto.*;
import it.gov.pagopa.postepay.entity.PostePayPayment;
import it.gov.pagopa.postepay.repository.PostePayPaymentRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.persistence.NoResultException;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/postepay")
@Log4j2
public class PostePayController {

    private static final String KO = "KO";
    private static final String POSTEPAY_ONBOARDING_OUTCOME_PROPERTY = "POSTEPAY_ONBOARDING_OUTCOME";
    private static final String POSTEPAY_PAYMENT_OUTCOME_PROPERTY = "POSTEPAY_PAYMENT_OUTCOME";
    private static final String POSTEPAY_PAYMENT_DETAILS_OUTCOME_PROPERTY = "POSTEPAY_PAYMENT_DETAILS_OUTCOME";
    private static final String POSTEPAY_REFUND_OUTCOME_PROPERTY = "POSTEPAY_REFUND_OUTCOME";
    private static final String POSTEPAY_PAYMENT_TIMEOUT_MS_PROPERTY = "POSTEPAY_PAYMENT_TIMEOUT_MS";
    private static final String POSTEPAY_REDIRECT_URL_PROPERTY = "POSTEPAY_REDIRECT_URL";
    private String paymentOutcomeConfig;
    private String refundOutcomeConfig;
    private String onboardingOutcomeConfig;
    private String detailsOutcomeConfig;
    private String redirectUrlConfig;

    @Autowired
    private TableConfigRepository configRepository;

    @Autowired
    private PostePayPaymentRepository paymentRepository;

    private void refreshConfigs() {
        redirectUrlConfig = configRepository.findByPropertyKey(POSTEPAY_REDIRECT_URL_PROPERTY).getPropertyValue();
        onboardingOutcomeConfig = configRepository.findByPropertyKey(POSTEPAY_ONBOARDING_OUTCOME_PROPERTY).getPropertyValue();
        paymentOutcomeConfig = configRepository.findByPropertyKey(POSTEPAY_PAYMENT_OUTCOME_PROPERTY).getPropertyValue();
        refundOutcomeConfig = configRepository.findByPropertyKey(POSTEPAY_REFUND_OUTCOME_PROPERTY).getPropertyValue();
        detailsOutcomeConfig = configRepository.findByPropertyKey(POSTEPAY_PAYMENT_DETAILS_OUTCOME_PROPERTY).getPropertyValue();
        String timeoutString = configRepository.findByPropertyKey(POSTEPAY_PAYMENT_TIMEOUT_MS_PROPERTY).getPropertyValue();
        try {
            int clientTimeout = Integer.parseInt(timeoutString);
            Thread.sleep(clientTimeout);
        } catch (InterruptedException e) {
            log.warn(e);
        }
    }

    @Transactional
    @PostMapping("/api/v1/payment/create")
    public ResponseEntity<Object> createPayment(@RequestBody @Valid CreatePaymentRequest request) {
        refreshConfigs();
        String paymentId = savePaymentRequest(request, false);
        log.info("CreatePaymentResponse: Payment id: " + paymentId + " - Redirect URL: " + redirectUrlConfig);
        if (KO.equals(paymentOutcomeConfig)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            "La risposta del mock-psp al pagamento è stata configurata per rispondere KO"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new CreatePaymentResponse(paymentId, redirectUrlConfig));
    }

    @Transactional
    @PostMapping("/api/v1/user/onboarding")
    public ResponseEntity<Object> onboarding(@RequestBody @Valid CreatePaymentRequest request) {
        refreshConfigs();
        String paymentId = savePaymentRequest(request, true);
        log.info("Onboarding response --> Payment id: " + paymentId + " - Redirect URL: " + redirectUrlConfig);
        if (KO.equals(onboardingOutcomeConfig)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            "La risposta del mock-psp all'onboarding è stata configurata per rispondere KO"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new CreatePaymentResponse(paymentId, redirectUrlConfig));
    }

    @PostMapping("/api/v1/payment/details")
    public ResponseEntity<Object> paymentDetails(@RequestBody @Valid DetailsPaymentRequest request) {
        log.info("START - postepay payment details");
        refreshConfigs();
        String paymentID = request.getPaymentID();
        try {
            PostePayPayment postePayPayment = paymentRepository.findByPaymentId(paymentID);
            return ResponseEntity.status(HttpStatus.OK).body(createDetailsResponse(postePayPayment));
        } catch (NoResultException e) {
            log.error("No entity found for paymentID " + paymentID, e);
            log.info("END - postepay payment details");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
                            HttpStatus.NOT_FOUND.getReasonPhrase(),
                            "Errore durante il recupero del pagamento con id " + paymentID));
        } catch (Exception e) {
            log.error("No entity found for paymentID " + paymentID, e);
            log.info("END - postepay payment details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            "Errore durante il recupero dei dettagli del pagamento " + paymentID));
        }
    }

    private DetailsPaymentResponse createDetailsResponse(PostePayPayment postePayPayment) {
        DetailsPaymentResponse detailsPaymentResponse = new DetailsPaymentResponse();
        detailsPaymentResponse.setShopId(postePayPayment.getShopId());
        detailsPaymentResponse.setShopTransactionId(postePayPayment.getShopTransactionId());
        detailsPaymentResponse.setPaymentID(postePayPayment.getPaymentId());
        detailsPaymentResponse.setResult(detailsOutcomeConfig);
        detailsPaymentResponse.setAuthNumber("authNumber");
        detailsPaymentResponse.setAmount("amount");
        detailsPaymentResponse.setDescription("mock-psp payment");
        detailsPaymentResponse.setCurrency("978");
        detailsPaymentResponse.setBuyerName("Mock PSP");
        detailsPaymentResponse.setBuyerEmail("mock-psp@mock.com");
        detailsPaymentResponse.setPaymentChannel("APP");
        detailsPaymentResponse.setAuthType("IMMEDIATA");
        detailsPaymentResponse.setStatus(postePayPayment.getOutcome());
        detailsPaymentResponse.setRefundedAmount(postePayPayment.isRefunded() ? "1234" : "0");
        return detailsPaymentResponse;
    }

    @PostMapping("/api/v1/payment/refund")
    public ResponseEntity<Object> refundPayment(@RequestBody @Valid RefundPaymentRequest request) {
        log.info("Starting PostePay refund");
        refreshConfigs();
        try {
            String paymentID = request.getPaymentID();
            PostePayPayment postePayPayment = paymentRepository.findByPaymentId(paymentID);

            if (ObjectUtils.isEmpty(postePayPayment)) {
                log.error("Payment " + paymentID + " not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                String.format("Il pagamento %s non è stato trovato", paymentID)));
            }

            if (postePayPayment.isRefunded()) {
                EsitoStorno outcome = postePayPayment.isRefunded() ? EsitoStorno.OK : EsitoStorno.KO;
                log.info("Refund request for paymentID " + paymentID + " already processed - outcome: " + outcome);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new RefundPaymentResponse(paymentID, postePayPayment.getShopTransactionId(), outcome));
            }

            EsitoStorno transactionResult = EsitoStorno.fromValue(refundOutcomeConfig);
            log.info("setting paymentId " + paymentID + " as refunded");
            postePayPayment.setRefunded(transactionResult.equals(EsitoStorno.OK));
            paymentRepository.save(postePayPayment);
            log.info("End PostePay refund");
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new RefundPaymentResponse(paymentID, postePayPayment.getShopTransactionId(), transactionResult));
        } catch (Exception e) {
            log.error("Exception while performing refund operation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            "Si è verificato un errore durante lo storno del pagamento"));
        }
    }

    private String savePaymentRequest(CreatePaymentRequest request, boolean isOnboarding) {
        PostePayPayment postePayPayment = new PostePayPayment();
        postePayPayment.setMerchantId(request.getMerchantId());
        postePayPayment.setShopId(request.getShopId());
        String paymentId = UUID.randomUUID().toString();
        postePayPayment.setPaymentId(paymentId);
        postePayPayment.setShopTransactionId(request.getShopTransactionId());
        postePayPayment.setOnboarding(isOnboarding);
        postePayPayment.setOutcome(paymentOutcomeConfig);
        paymentRepository.save(postePayPayment);
        return paymentId;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, ValidationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("1", "Errore", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("0", "Errore", e.getMessage()));
    }
}
