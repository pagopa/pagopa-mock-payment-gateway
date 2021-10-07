package it.gov.pagopa.paypalpsp.dto.dtoenum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.gov.pagopa.db.entityenum.ApiPaypalIdEnum.*;

@ToString
@AllArgsConstructor
public enum PpResponseErrCode {
    TIMEOUT("-1", "", HttpStatus.GATEWAY_TIMEOUT, Arrays.asList(ONBOARDING, PAYMENT, REFUND)), //out of psp scope
    INPUT_NON_JSON("9", "input non in formato json", HttpStatus.BAD_REQUEST, Arrays.asList(ONBOARDING, PAYMENT, REFUND)),
    ID_BACK_USATO_NON_VALIDO("10", " id_back non valido o utilizzato", null, Arrays.asList(ONBOARDING, PAYMENT)),
    PARAMETRI_NON_VALIDI("11", "parametri non validi", HttpStatus.BAD_REQUEST, Arrays.asList(ONBOARDING, PAYMENT, REFUND)),
    AUTORIZZAZIONE_NEGATA("13", "autorizzazione negata", HttpStatus.UNAUTHORIZED, Arrays.asList(ONBOARDING, PAYMENT, REFUND)),
    AUTORIZZAZIONE_IP_NEGATA("15", "autorizzazione ip negata", HttpStatus.UNAUTHORIZED, Arrays.asList(ONBOARDING, PAYMENT, REFUND)),
    TOKEN_PAYPAL_NON_VALIDO("16", " token paypal non valido", HttpStatus.UNPROCESSABLE_ENTITY, Arrays.asList(ONBOARDING)),
    TIMESTAMP_SCADUTO("17", "timestamp scaduto", null, Arrays.asList(PAYMENT)),
    ID_APP_IO_NON_ESISTE("18", " id_appio non esiste", HttpStatus.UNPROCESSABLE_ENTITY, Arrays.asList(PAYMENT)),
    CODICE_CONTRATTO_PRESENTE("19", "id_appio con bilagr già associato", HttpStatus.UNPROCESSABLE_ENTITY, null),
    ID_TRS_NON_VALIDO("22", " id_trs_appio non valido", HttpStatus.UNPROCESSABLE_ENTITY, Arrays.asList(REFUND)),
    ACQUIRE_REFUND_NON_VALIDO("24", " acquire_refund non valido", HttpStatus.UNPROCESSABLE_ENTITY, Arrays.asList(REFUND)),
    PAYPAL_GET_TOKEN_KO("31", " paypal get api token KO", null, Arrays.asList(PAYMENT, REFUND)),
    PAYPAL_CREATE_AGR_TOKEN_KO("33", " paypal create billing agr token KO", null, Arrays.asList(PAYMENT)),
    PAYPAL_CREATE_AGR_TOKEN_KO_2("34", "paypal create billing agr token KO", null, Arrays.asList(PAYMENT)),
    PAYPAL_CREATE_AGR_TOKEN_KO_3("35", "paypal create billing agr token KO", null, Arrays.asList(PAYMENT)),
    PARAMETRI_NON_VALIDI_2("51", "parametri non validi", null, Arrays.asList(PAYMENT)),
    AUTORIZZAZIONE_NEGATA_2("53", "autorizzazione negata", null, Arrays.asList(PAYMENT)),
    RITORNO_ANOMALO("57", "ritorno paypal anomalo", null, Arrays.asList(PAYMENT)),
    PAYPAL_GET_TOKEN_KO_2("61", " paypal get api token KO", null, Arrays.asList(PAYMENT)),
    PAYPAL_REFUND_KO_1("63", "refund error KO", null, Arrays.asList(REFUND)),
    PAYPAL_REFUND_KO_2("64", "refund error KO", null, Arrays.asList(REFUND)),
    PAYPAL_CREATE_AGR_ID_KO_3("65", "paypal api create billing agr id KO", null, Arrays.asList(PAYMENT, REFUND)),
    PAYPAL_REFUND_KO_3("69", "refund error KO", null, Arrays.asList(REFUND)),
    DB_INTERNAL_ERROR("67", "db internal error", HttpStatus.INTERNAL_SERVER_ERROR, Arrays.asList(ONBOARDING, PAYMENT, REFUND));

    @JsonValue
    @Getter
    private String code;

    @Getter
    private String description;

    @Getter
    private HttpStatus httpStatus;

    @Getter
    private List<ApiPaypalIdEnum> allowed;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PpResponseErrCode of(String code) {
        if (code == null) return null;
        return Stream.of(PpResponseErrCode.values())
                .filter(p -> StringUtils.equalsIgnoreCase(p.getCode(), code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static List<PpResponseErrCode> onboardingValues() {
        return valuesByApiId(ONBOARDING);
    }

    public static List<PpResponseErrCode> paymentValues() {
        return valuesByApiId(PAYMENT);
    }

    private static List<PpResponseErrCode> valuesByApiId(ApiPaypalIdEnum apiPaypalIdEnum) {
        return Stream.of(PpResponseErrCode.values())
                .filter(e -> e.getAllowed() != null
                        && e.getAllowed().stream().anyMatch(apiPaypalIdEnum::equals))
                .collect(Collectors.toList());
    }


}
