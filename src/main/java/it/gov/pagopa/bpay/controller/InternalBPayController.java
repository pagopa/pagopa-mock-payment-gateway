package it.gov.pagopa.bpay.controller;

import it.gov.pagopa.bpay.dto.*;
import it.gov.pagopa.db.entity.*;
import it.gov.pagopa.db.repository.*;
import lombok.extern.log4j.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

import javax.annotation.*;

@Log4j2
@Controller
@RequestMapping("/bpayweb/management")
public class InternalBPayController {

    @Autowired
    private TableConfigRepository configRepository;

    @Autowired
    private BPayController bPayController;

    @Value("${server.public-url}")
    private String publicUrl;

    @PostConstruct
    public void init() {
        outcomeConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_OUTCOME");
        timeoutConfig = configRepository.findByPropertyKey("BPAY_PAYMENT_TIMEOUT");
    }

    private TableConfig outcomeConfig;
    private TableConfig timeoutConfig;

    @PostMapping(value = "/outcome", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String changeOutcome(CallBPayRequest request, Model model) {
        String code = request.getOutcome();
        if (!code.equals(outcomeConfig.getPropertyValue())) {
            outcomeConfig.setPropertyValue(code);
            configRepository.save(outcomeConfig);
        }
        boolean timeout = request.isTimeout();
        if (timeout != BooleanUtils.toBoolean(timeoutConfig.getPropertyValue())) {
            timeoutConfig.setPropertyValue(BooleanUtils.toStringTrueFalse(timeout));
            configRepository.save(timeoutConfig);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        HttpEntity<String> soapRequest = new HttpEntity<>(request.getRequest(), headers);
        String result = new RestTemplate().postForObject(publicUrl + "/bpay", soapRequest, String.class);
        model.addAttribute("result", result);
        return "bpay/home.html";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("callBPayRequest", new CallBPayRequest());
        return "bpay/home.html";
    }

}
