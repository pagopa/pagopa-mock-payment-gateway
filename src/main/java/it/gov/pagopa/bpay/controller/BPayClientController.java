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
public class BPayClientController {

    @Autowired
    private TableConfigRepository configRepository;

    private TableConfig outcomeConfig;
    private TableConfig timeoutConfig;

    @PostConstruct
    public void init() {
        outcomeConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_OUTCOME");
        timeoutConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_TIMEOUT");
    }

    @PostMapping("/client")
    public void changeClient(@RequestBody String client) {
        TableConfig currentClient = configRepository.findByPropertyKey("BPAY_CURRENT_CLIENT");
        currentClient.setPropertyValue(client);
        configRepository.save(currentClient);
    }

    @PostMapping("/outcome")
    public void changeOutcome(@RequestParam(required = false) String code, @RequestParam(required = false) String timeout) {
        outcomeConfig.setPropertyValue(code);
        configRepository.save(outcomeConfig);
        timeoutConfig.setPropertyValue(timeout != null ? timeout : "false");
        configRepository.save(timeoutConfig);
    }

}
