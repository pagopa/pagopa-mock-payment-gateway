package it.gov.pagopa.vpos.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CreditCard {
    private static final Map<String, String> CREDIT_CARDS = new HashMap<>();

    public static void addCreditCard(String card, String result) {
        CREDIT_CARDS.put(card, result);
    }

    public static String getResult(String card) {
        return StringUtils.firstNonBlank(CREDIT_CARDS.get(card), "20");
    }
}
