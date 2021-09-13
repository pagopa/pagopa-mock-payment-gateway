package it.gov.pagopa.paypalpsp;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/paypalweb")
public class PayPalWebController {
    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/test")
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }
}