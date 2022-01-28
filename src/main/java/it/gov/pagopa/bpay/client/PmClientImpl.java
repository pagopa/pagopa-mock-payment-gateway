package it.gov.pagopa.bpay.client;

import feign.*;
import feign.jackson.*;
import it.gov.pagopa.bpay.dto.*;
import it.gov.pagopa.bpay.entity.*;
import it.gov.pagopa.db.repository.*;
import lombok.extern.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import javax.annotation.*;
import java.util.concurrent.*;

@Log4j2
@Component
public class PmClientImpl {

    @Autowired
    private TableConfigRepository tableConfigRepository;

    @PostConstruct
    public void init() {
        pmClient = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(PmClient.class, tableConfigRepository.findByPropertyKey("BPAY_CALLBACK_BASE_PATH").getPropertyValue());
    }

    private PmClient pmClient;

    @Async
    public void callbackPm(BPayPayment payment) throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextLong(10000));
        log.info("Calling PM...");
        TransactionUpdateRequest request = new TransactionUpdateRequest(payment.getCorrelationId());
        pmClient.updateTransaction(payment.getIdPagoPa(), request);
    }

}
