package it.gov.pagopa.bpay.controller;

import it.gov.pagopa.db.entity.*;
import it.gov.pagopa.db.repository.*;
import lombok.extern.log4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/bpayweb/client")
public class BPayClientController {

    @Autowired
    private TableConfigRepository configRepository;

    @PostMapping("/change")
    public void changeClient(@RequestBody String client) {
        TableConfig currentClient = configRepository.findByPropertyKey("BPAY_CURRENT_CLIENT");
        currentClient.setPropertyValue(client);
        configRepository.save(currentClient);
    }

}
