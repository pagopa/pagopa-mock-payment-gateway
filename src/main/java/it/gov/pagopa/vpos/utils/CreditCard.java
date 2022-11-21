package it.gov.pagopa.vpos.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CreditCard {
    private static Map<String, String> creditCards = new HashMap<>();

    public static void addCreditCard(String card, String result) {
        creditCards.put(card, result);
    }

    public static String getResult(String card) {
        return StringUtils.firstNonBlank(creditCards.get(card), "20");
    }
}
