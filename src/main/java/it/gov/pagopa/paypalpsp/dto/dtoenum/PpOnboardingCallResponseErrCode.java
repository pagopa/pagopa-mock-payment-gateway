package it.gov.pagopa.paypalpsp.dto.dtoenum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@AllArgsConstructor
public enum PpOnboardingCallResponseErrCode {
    ID_BACK_USATO_NON_VALIDO("10", " id_back non valido o utilizzato"),
    PARAMETRI_NON_VALIDI("11", "parametri non validi"),
    AUTORIZZAZIONE_IP_NEGATA("13", "autorizzazione negata"),
    CODICE_CONTRATTO_PRESENTE("15", "autorizzazione ip negata"),
    TIMESTAMP_SCADUTO("17", "timestamp scaduto"),
    PAYPAL_GET_TOKEN_KO("31", " paypal get api token KO"),
    PAYPAL_CREATE_AGR_TOKEN_KO("33", " paypal create billing agr token KO"),
    PAYPAL_CREATE_AGR_TOKEN_KO_2("34", "paypal create billing agr token KO"),
    PAYPAL_CREATE_AGR_TOKEN_KO_3("35", "paypal create billing agr token KO"),
    PARAMETRI_NON_VALIDI_2("51", "parametri non validi"),
    AUTORIZZAZIONE_IP_NEGATA_2("53", "autorizzazione negata"),
    RITORNO_ANOMALO("57", "ritorno paypal anomalo"),
    PAYPAL_GET_TOKEN_KO_2("61", " paypal get api token KO"),
    PAYPAL_CREATE_AGR_ID_KO_3("65", "paypal api create billing agr id KO"),
    DB_INTERNAL_ERROR("67", "db internal error");

    @Getter
    private String code;
    @Getter
    private String description;

    public static PpOnboardingCallResponseErrCode of(String code) {
        return Stream.of(PpOnboardingCallResponseErrCode.values())
                .filter(p -> StringUtils.equalsIgnoreCase(p.getCode(), code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
