package it.gov.pagopa.postepay.controller;

import it.gov.pagopa.db.repository.*;
import it.gov.pagopa.postepay.dto.*;
import it.gov.pagopa.postepay.entity.*;
import it.gov.pagopa.postepay.repository.*;
import lombok.extern.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.transaction.annotation.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.*;

import javax.validation.*;
import java.util.*;

@Validated
@RestController
@RequestMapping("/postepay")
@Log4j2
public class PostePayController {

    @Autowired
    private TableConfigRepository configRepository;

    @Autowired
    private PostePayPaymentRepository paymentRepository;

    private String paymentOutcomeConfig;

    @PostMapping("/api/v1/payment/create")
    @Transactional
    public ResponseEntity<Object> createPayment(@RequestBody @Valid CreatePaymentRequest request) {
        refreshConfigs();
        String paymentId = UUID.randomUUID().toString();
        String redirectUrl = configRepository.findByPropertyKey("POSTEPAY_REDIRECT_URL").getPropertyValue();
        log.info("CreatePaymentResponse: Payment id: " + paymentId + " - Redirect URL: " + redirectUrl);
        PostePayPayment postePayPayment = new PostePayPayment();
        postePayPayment.setPaymentId(paymentId);
        postePayPayment.setShopTransactionId(request.getShopTransactionId());
        postePayPayment.setOutcome(paymentOutcomeConfig);
        paymentRepository.save(postePayPayment);
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
        try {
            Thread.sleep(Integer.parseInt(configRepository.findByPropertyKey("POSTEPAY_PAYMENT_TIMEOUT_MS").getPropertyValue()));
        } catch (InterruptedException e) {
            log.warn(e);
        }
    }


    @PostMapping("/api/v1/payment/refund")
    public ResponseEntity<Object> refundPayment(@RequestBody @Valid DetailsPaymentRequest request) {
        String shopId = request.getShopId();
        String paymentId = request.getPaymentID();
        String shopTransactionId = request.getShopTransactionId();
        log.info("START refundPayment - DetailsPaymentRequest: "
                + "ShopId: " + shopId
                + " - PaymentOd: " + paymentId
                + " - shopTransactionId " + shopTransactionId);

        return ResponseEntity.status(HttpStatus.OK).
                body(new DetailsPaymentResponse(paymentId, shopTransactionId, EsitoStorno.OK));
    }


}
