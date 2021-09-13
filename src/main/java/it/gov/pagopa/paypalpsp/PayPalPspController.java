package it.gov.pagopa.paypalpsp;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PayPalPspController {
    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/test")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }
}