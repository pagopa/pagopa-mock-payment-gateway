package it.gov.pagopa.postepay.controller;

import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.postepay.dto.*;
import it.gov.pagopa.postepay.entity.PostePayPayment;
import it.gov.pagopa.postepay.repository.PostePayPaymentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
    private static final String POSTEPAY_REFUND_OUTCOME_PROPERTY = "POSTEPAY_REFUND_OUTCOME";
    private static final String POSTEPAY_PAYMENT_TIMEOUT_MS_PROPERTY = "POSTEPAY_PAYMENT_TIMEOUT_MS";
    private static final String POSTEPAY_REDIRECT_URL_PROPERTY = "POSTEPAY_REDIRECT_URL";
    private String paymentOutcomeConfig;
    private String refundOutcomeConfig;
    private String onboardingOutcomeConfig;

    @Autowired
    private TableConfigRepository configRepository;

    @Autowired
    private PostePayPaymentRepository paymentRepository;

    private void refreshConfigs() {
        onboardingOutcomeConfig = configRepository.findByPropertyKey(POSTEPAY_ONBOARDING_OUTCOME_PROPERTY).getPropertyValue();
        paymentOutcomeConfig = configRepository.findByPropertyKey(POSTEPAY_PAYMENT_OUTCOME_PROPERTY).getPropertyValue();
        refundOutcomeConfig = configRepository.findByPropertyKey(POSTEPAY_REFUND_OUTCOME_PROPERTY).getPropertyValue();
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
    public ResponseEntity<Object> createPayment(@RequestBody @Valid CreatePaymentRequest request) throws Exception {
        refreshConfigs();
        String redirectUrl = configRepository.findByPropertyKey(POSTEPAY_REDIRECT_URL_PROPERTY).getPropertyValue();
        String paymentId = savePaymentRequest(request, false);
        log.info("CreatePaymentResponse: Payment id: " + paymentId + " - Redirect URL: " + redirectUrl);
        if (KO.equals(paymentOutcomeConfig)) {
            throw new Exception(KO);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new CreatePaymentResponse(paymentId, redirectUrl));
    }

    @Transactional
    @PostMapping("/api/v1/user/onboarding")
    public ResponseEntity<Object> onboarding(@RequestBody @Valid CreatePaymentRequest request) throws Exception {
        refreshConfigs();
        String redirectUrl = configRepository.findByPropertyKey(POSTEPAY_REDIRECT_URL_PROPERTY).getPropertyValue();
        String paymentId = savePaymentRequest(request, true);
        log.info("Onboarding response --> Payment id: " + paymentId + " - Redirect URL: " + redirectUrl);
        if (KO.equals(onboardingOutcomeConfig)) {
            throw new Exception(KO);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new CreatePaymentResponse(paymentId, redirectUrl));
    }

    @PostMapping("/api/v1/payment/refund")
    public ResponseEntity<Object> refundPayment(@RequestBody @Valid RefundPaymentRequest request) {
        log.info("Starting PostePay refund");
        refreshConfigs();
        try {
            PostePayPayment postePayPayment = paymentRepository.findByPaymentId(request.getPaymentID());
            String paymentId = postePayPayment.getPaymentId();
            EsitoStorno transactionResult = EsitoStorno.fromValue(refundOutcomeConfig);
            RefundPaymentResponse refundPaymentResponse = new RefundPaymentResponse(paymentId, postePayPayment.getShopTransactionId(), transactionResult);
            log.info("setting paymentId " + paymentId + " as refunded");
            postePayPayment.setIsRefunded(transactionResult.equals(EsitoStorno.OK));
            paymentRepository.save(postePayPayment);
            log.info("End PostePay refund");
            return ResponseEntity.status(HttpStatus.OK).body(refundPaymentResponse);
        } catch (Exception e) {
            log.error("Exception while performing refund operation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            "Errore durante il recupero del pagamento"));
        }
    }

    private String savePaymentRequest(CreatePaymentRequest request, boolean isOnboarding) {
        PostePayPayment postePayPayment = new PostePayPayment();
        postePayPayment.setMerchantId(request.getMerchantId());
        String paymentId = UUID.randomUUID().toString();
        postePayPayment.setPaymentId(paymentId);
        postePayPayment.setShopTransactionId(request.getShopTransactionId());
        postePayPayment.setIsOnboarding(isOnboarding);
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
