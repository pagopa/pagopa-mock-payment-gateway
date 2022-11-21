package it.gov.pagopa.vpos.controller;

import it.gov.pagopa.vpos.dto.CreditCardResponse;
import it.gov.pagopa.vpos.utils.VposCreditCardGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(value = "/cc")
public class CreditCardRestController {

    private static final String VISA = "visa";
    private static final String MASTERCARD = "mastercard";
    private static final String MAESTRO = "maestro";
    private static final String AMEX = "amex";
    private static final String DINERS = "diners";
    private static final CreditCardResponse CREDIT_CARD_RESPONSE_DEFAULT = new CreditCardResponse(StringUtils.EMPTY, StringUtils.EMPTY);
    private static final String RAND = "rand";
    private static final List<String> POSSIBLE_CC_TYPE = Arrays.asList(VISA, MASTERCARD, MAESTRO, AMEX, DINERS);

    @GetMapping("generateCard")
    public CreditCardResponse generateCard(@RequestParam String type) {
        CreditCardResponse creditCardResponse;

        switch (type) {
            case VISA:
                String visaCardNumber = VposCreditCardGenerator.generateVisaCardNumber();
                creditCardResponse = new CreditCardResponse(visaCardNumber, getUrlImg(VISA));
                break;
            case MASTERCARD:
                String masterCardNumber = VposCreditCardGenerator.generateMasterCardNumber();
                creditCardResponse = new CreditCardResponse(masterCardNumber, getUrlImg(MASTERCARD));
                break;
            case MAESTRO:
                String maestroCardNumber = VposCreditCardGenerator.generateMaestroCardNumber();
                creditCardResponse = new CreditCardResponse(maestroCardNumber, getUrlImg(MAESTRO));
                break;
            case AMEX:
                String amexCardNumber = VposCreditCardGenerator.generateAmexCardNumber();
                creditCardResponse = new CreditCardResponse(amexCardNumber, getUrlImg(AMEX));
                break;
            case DINERS:
                String dinersCardNumber = VposCreditCardGenerator.generateDinersCardNumber();
                creditCardResponse = new CreditCardResponse(dinersCardNumber, getUrlImg(DINERS));
                break;
            case RAND:
                creditCardResponse = generateCard(POSSIBLE_CC_TYPE.get(new Random().nextInt(POSSIBLE_CC_TYPE.size())));
                break;
            default:
                creditCardResponse = CREDIT_CARD_RESPONSE_DEFAULT;
                break;
        }

        return creditCardResponse;
    }

    private String getUrlImg(String imgName) {
        return String.format("/static/img/%s.jpg", imgName);
    }
}
