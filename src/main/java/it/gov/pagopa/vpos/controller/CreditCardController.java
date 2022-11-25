package it.gov.pagopa.vpos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CreditCardController {
    @Value("${mock-pgs-url}")
    private String mockPgsUrl;

    @GetMapping("/cc")
    public String getCc(Model model) {
        model.addAttribute("MOCK_PGS_URL", mockPgsUrl);
        return "vpos/cc.html";
    }
}
