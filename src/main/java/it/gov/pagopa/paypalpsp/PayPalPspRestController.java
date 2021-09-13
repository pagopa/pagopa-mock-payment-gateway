package it.gov.pagopa.paypalpsp;


import it.gov.pagopa.Prova;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PayPalPspRestController {
    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/testrest")
    public Prova homePage() {
        Prova prova= new Prova();
        prova.setNome(appName);
        return prova;
    }
}