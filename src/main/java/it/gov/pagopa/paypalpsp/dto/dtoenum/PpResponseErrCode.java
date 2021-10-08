package it.gov.pagopa.paypalpsp.dto.dtoenum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.gov.pagopa.db.entityenum.ApiPaypalIdEnum.*;

@ToString
@AllArgsConstructor
public enum PpResponseErrCode {
    TIMEOUT("-1", "", HttpStatus.GATEWAY_TIMEOUT, Arrays.asList(ONBOARDING, PAYMENT, REFUND, DELETE)), //out of psp scope
    INPUT_NON_JSON("909", "input non in formato json", HttpStatus.BAD_REQUEST, Arrays.asList(ONBOARDING, PAYMENT, REFUND, DELETE)),
    PARAMETRI_NON_VALIDI("911", "parametri non validi", HttpStatus.BAD_REQUEST, Arrays.asList(ONBOARDING, PAYMENT, REFUND, DELETE)),
    AUTORIZZAZIONE_NEGATA("913", "autorizzazione negata", HttpStatus.UNAUTHORIZED, Arrays.asList(ONBOARDING, PAYMENT, REFUND, DELETE, ONBOARDING_REDIRECT)),
    AUTORIZZAZIONE_IP_NEGATA("915", "autorizzazione ip negata", HttpStatus.UNAUTHORIZED, Arrays.asList(ONBOARDING, PAYMENT, REFUND, DELETE, ONBOARDING_REDIRECT)),
    PAYPAL_GET_TOKEN_KO("931", "paypal get api token KO", HttpStatus.INTERNAL_SERVER_ERROR, Arrays.asList(ONBOARDING, PAYMENT, REFUND, DELETE)),
    DB_INTERNAL_ERROR("967", "db internal error", HttpStatus.INTERNAL_SERVER_ERROR, Arrays.asList(ONBOARDING, PAYMENT, REFUND, DELETE, ONBOARDING_REDIRECT)),

    TOKEN_PAYPAL_NON_VALIDO("116", "token paypal non valido", HttpStatus.INTERNAL_SERVER_ERROR, Collections.singletonList(ONBOARDING)),
    CODICE_CONTRATTO_PRESENTE("119", "id_appio con bilagr già associato", HttpStatus.UNPROCESSABLE_ENTITY, Collections.singletonList(ONBOARDING)),

    ID_BACK_NON_VALIDO("210", "id_back non valido o utilizzato", null, Collections.singletonList(ONBOARDING_REDIRECT)),
    TIMESTAMP_SCADUTO("217", "timestamp scaduto", null, Collections.singletonList(ONBOARDING_REDIRECT)),
    PAYPAL_CREATE_AGR_TOKEN_KO("233", "paypal create billing agr token KO", null, Collections.singletonList(ONBOARDING_REDIRECT)),
    RITORNO_PAYPAL_ANOMALO("257", "ritorno paypal anomalo", null, Collections.singletonList(ONBOARDING_REDIRECT)),
    PAYPAL_CREATE_AGR_ID_KO_3("265", "paypal api create billing agr id KO", null, Collections.singletonList(ONBOARDING_REDIRECT)),

    DELETE_ID_APP_IO_NON_ESISTE("318", "id_appio non esiste o bilagr non valido", HttpStatus.NOT_FOUND, Collections.singletonList(DELETE)),
    DELETE_BILAGR_KO("31", "paypal bilagr delete KO", HttpStatus.INTERNAL_SERVER_ERROR, Collections.singletonList(DELETE)),

    PAYMENT_ID_APP_IO_NON_ESISTE("318", "id_appio non esiste", HttpStatus.NOT_FOUND, Collections.singletonList(PAYMENT)),
    FEE_NON_VALIDA("419", "fee non valida", HttpStatus.BAD_REQUEST, Collections.singletonList(PAYMENT)),
    BILL_AGR_NON_TROVATO("421", "bilagr non trovato", HttpStatus.NOT_FOUND, Collections.singletonList(PAYMENT)),
    LIMITE_IMPORTO_SUPERATO("423", "limite importo superato", HttpStatus.UNPROCESSABLE_ENTITY, Collections.singletonList(PAYMENT)),
    PAGAMENTO_DIRETTO_KO_BILL_AGR("463", "paypal pagamento diretto KO (Invalid billing_agreement_id)", HttpStatus.UNPROCESSABLE_ENTITY, Collections.singletonList(PAYMENT)),
    PAGAMENTO_DIRETTO_KO("465", "paypal pagamento diretto KO", HttpStatus.UNPROCESSABLE_ENTITY, Collections.singletonList(PAYMENT)),

    ID_TRS_NON_VALIDO("518", "id_trs_appio non valido", HttpStatus.NOT_FOUND, Collections.singletonList(REFUND)),
    ACQUIRE_REFUND_NON_VALIDO("521", "acquire_refund non valido", HttpStatus.UNPROCESSABLE_ENTITY, Collections.singletonList(REFUND)),
    PAYPAL_REFUND_KO("565", "refund error KO", HttpStatus.UNPROCESSABLE_ENTITY, Collections.singletonList(REFUND));

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

    public static List<PpResponseErrCode> onboardingRedirectValues() {
        return valuesByApiId(ONBOARDING_REDIRECT);
    }

    public static List<PpResponseErrCode> valuesByApiId(ApiPaypalIdEnum apiPaypalIdEnum) {
        return Stream.of(PpResponseErrCode.values())
                .filter(e -> e.getAllowed() != null
                        && e.getAllowed().stream().anyMatch(apiPaypalIdEnum::equals))
                .collect(Collectors.toList());
    }


}
