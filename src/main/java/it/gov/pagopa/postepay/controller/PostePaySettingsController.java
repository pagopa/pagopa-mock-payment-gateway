package it.gov.pagopa.postepay.controller;

import it.gov.pagopa.db.entity.*;
import it.gov.pagopa.db.repository.*;
import lombok.extern.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.*;

@Log4j2
@RestController
@RequestMapping("/postepayweb/change")
public class PostePaySettingsController {

    @Autowired
    private TableConfigRepository configRepository;

    private TableConfig paymentOutcomeConfig;

    private TableConfig timeoutConfig;

    @PostConstruct
    public void init() {
        paymentOutcomeConfig = configRepository.findByPropertyKey("POSTEPAY_PAYMENT_OUTCOME");
        timeoutConfig = configRepository.findByPropertyKey("POSTEPAY_PAYMENT_TIMEOUT_MS");
    }

    @PostMapping("/outcome")
    public void changeOutcome(@RequestParam(required = false) String paymentOutcome, @RequestParam(required = false) Integer timeoutMs) {
        if (paymentOutcome != null) {
            paymentOutcomeConfig.setPropertyValue(paymentOutcome);
            configRepository.save(paymentOutcomeConfig);
        }
        if (timeoutMs != null) {
            timeoutConfig.setPropertyValue(timeoutMs.toString());
            configRepository.save(timeoutConfig);
        }
    }

}
