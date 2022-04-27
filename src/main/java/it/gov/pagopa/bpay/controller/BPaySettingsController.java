package it.gov.pagopa.bpay.controller;

import it.gov.pagopa.db.entity.*;
import it.gov.pagopa.db.repository.*;
import lombok.extern.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.*;

@Log4j2
@RestController
@RequestMapping("/bpayweb/change")
public class BPaySettingsController {

    @Autowired
    private TableConfigRepository configRepository;

    private TableConfig paymentOutcomeConfig;

    private TableConfig refundOutcomeConfig;

    private TableConfig inquiryOutcomeConfig;
    private TableConfig timeoutConfig;

    @PostConstruct
    public void init() {
        paymentOutcomeConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_OUTCOME");
        refundOutcomeConfig = configRepository.findByPropertyKey("BPAY_REFUND_OUTCOME");
        inquiryOutcomeConfig = configRepository.findByPropertyKey("BPAY_INQUIRY_OUTCOME");
        timeoutConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_TIMEOUT_MS");
    }

    @PostMapping("/client")
    public void changeClient(@RequestBody String client) {
        TableConfig currentClient = configRepository.findByPropertyKey("BPAY_CURRENT_CLIENT");
        currentClient.setPropertyValue(client);
        configRepository.save(currentClient);
    }

    @PostMapping("/outcome")
    public void changeOutcome(@RequestParam(required = false) String paymentOutcome, @RequestParam(required = false) String refundOutcome, @RequestParam(required = false) String inquiryOutcome, @RequestParam(required = false) Integer timeoutMs) {
        if (paymentOutcome != null) {
            paymentOutcomeConfig.setPropertyValue(paymentOutcome);
            configRepository.save(paymentOutcomeConfig);
        }
        if (refundOutcome != null) {
            refundOutcomeConfig.setPropertyValue(refundOutcome);
            configRepository.save(refundOutcomeConfig);
        }
        if (inquiryOutcome != null) {
            inquiryOutcomeConfig.setPropertyValue(inquiryOutcome);
            configRepository.save(inquiryOutcomeConfig);
        }
        if (timeoutMs != null) {
            timeoutConfig.setPropertyValue(timeoutMs.toString());
            configRepository.save(timeoutConfig);
        }
    }

}
