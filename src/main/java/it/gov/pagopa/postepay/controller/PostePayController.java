package it.gov.pagopa.postepay.controller;

import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.postepay.dto.*;
import it.gov.pagopa.postepay.entity.PostePayPayment;
import it.gov.pagopa.postepay.repository.PostePayPaymentRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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

    private static final String VALIDATION_ERROR_MSG = "Il parametro %s non può essere blank";
    @Autowired
    private TableConfigRepository configRepository;

    @Autowired
    private PostePayPaymentRepository paymentRepository;

    private String paymentOutcomeConfig;
    private String refundOutcomeConfig;

    @PostMapping("/api/v1/payment/create")
    @Transactional
    public ResponseEntity<Object> createPayment(@RequestBody @Valid CreatePaymentRequest request) throws Exception {
        refreshConfigs();
        String paymentId = UUID.randomUUID().toString();
        String redirectUrl = configRepository.findByPropertyKey("POSTEPAY_REDIRECT_URL").getPropertyValue();
        log.info("CreatePaymentResponse: Payment id: " + paymentId + " - Redirect URL: " + redirectUrl);
        PostePayPayment postePayPayment = new PostePayPayment();
        postePayPayment.setPaymentId(paymentId);
        postePayPayment.setShopTransactionId(request.getShopTransactionId());
        postePayPayment.setOutcome(paymentOutcomeConfig);
        paymentRepository.save(postePayPayment);
        if ("KO".equals(paymentOutcomeConfig)) {
            throw new Exception("KO");
        }
        return ResponseEntity.status(HttpStatus.OK).body(new CreatePaymentResponse(paymentId, redirectUrl));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, ValidationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("1", "Errore", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("0", "Errore", e.getMessage()));
    }

    private void refreshConfigs() {
        paymentOutcomeConfig = configRepository.findByPropertyKey("POSTEPAY_PAYMENT_OUTCOME").getPropertyValue();
        refundOutcomeConfig = configRepository.findByPropertyKey("POSTEPAY_REFUND_OUTCOME").getPropertyValue();
        try {
            int clientTimeout = Integer.parseInt(configRepository.findByPropertyKey("POSTEPAY_PAYMENT_TIMEOUT_MS").getPropertyValue());
            Thread.sleep(clientTimeout);
        } catch (InterruptedException e) {
            log.warn(e);
        }
    }


    @PostMapping("/api/v1/payment/refund")
    public ResponseEntity<Object> refundPayment(@RequestBody RefundPaymentRequest request) {
        log.info("Starting PostePay refund");
        ResponseEntity<Object> badRequestEntity = validateRequestParameters(request);
        if (badRequestEntity != null) {
            log.error("Request parameter validation not passed");
            return badRequestEntity;
        }
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
            return createErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante il recupero del pagamento");
        }
    }

    private ResponseEntity<Object> validateRequestParameters(RefundPaymentRequest request) {
        if (StringUtils.isEmpty(request.getMerchantId())) {
            return createErrorResponseEntity(HttpStatus.BAD_REQUEST, String.format(VALIDATION_ERROR_MSG, "merchantId"));
        }
        if (StringUtils.isEmpty(request.getShopId())) {
            return createErrorResponseEntity(HttpStatus.BAD_REQUEST, String.format(VALIDATION_ERROR_MSG, "shopId"));
        }
        if (StringUtils.isEmpty(request.getShopTransactionId())) {
            return createErrorResponseEntity(HttpStatus.BAD_REQUEST, String.format(VALIDATION_ERROR_MSG, "shopTransactionId"));
        }
        if (StringUtils.isEmpty(request.getCurrency())) {
            return createErrorResponseEntity(HttpStatus.BAD_REQUEST, String.format(VALIDATION_ERROR_MSG, "currency"));
        }
        if (StringUtils.isEmpty(request.getPaymentID())) {
            return createErrorResponseEntity(HttpStatus.BAD_REQUEST, String.format(VALIDATION_ERROR_MSG, "paymentID"));
        }
        return null;
    }

    private ResponseEntity<Object> createErrorResponseEntity(HttpStatus httpStatus, String errorMessage) {
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(String.valueOf(httpStatus.value()), httpStatus.getReasonPhrase(), errorMessage));
    }


}
