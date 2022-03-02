package it.gov.pagopa.bpay.client;

import feign.*;
import feign.jackson.*;
import it.gov.pagopa.bpay.dto.*;
import it.gov.pagopa.bpay.entity.*;
import lombok.extern.log4j.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

@Log4j2
@Component
public class PmClientImpl {

    @Async
    public void callbackPm(BPayPayment payment) {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            log.error(e);
        }
        log.info("Calling PM...");
        PmClient pmClient = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .requestInterceptor(t -> t.header("X-Correlation-ID", payment.getCorrelationId()))
                .target(PmClient.class, payment.getClientHostname());
        TransactionUpdateRequest request = new TransactionUpdateRequest(payment.getOutcome().equals("0") ? "OK" : "KO", payment.getOutcome().equals("0") ? "authcode" : null);
        pmClient.updateTransaction(request);
    }

}
