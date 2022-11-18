package it.gov.pagopa.vpos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreditCardController {
    @GetMapping("/cc")
    public String getCc() {
        return "vpos/cc.html";
    }
}
