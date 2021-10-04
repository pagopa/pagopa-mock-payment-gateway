package it.gov.pagopa.paypalpsp.dto.dtoenum;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

@AllArgsConstructor
public enum PpOnboardingBackResponseErrCode {
    TIMEOUT("-1", "", HttpStatus.GATEWAY_TIMEOUT), //out of psp scope
    INPUT_NON_JSON("9", "input non in formato json", HttpStatus.BAD_REQUEST),
    PARAMETRI_NON_VALIDI("11", "parametri non validi", HttpStatus.BAD_REQUEST),
    AUTORIZZAZIONE_NEGATA("13", "autorizzazione negata ", HttpStatus.UNAUTHORIZED),
    AUTORIZZAZIONE_IP_NEGATA("15", "autorizzazione ip negata", HttpStatus.UNAUTHORIZED),
    TOKEN_PAYPAL_NON_VALIDO("16", " token paypal non valido", HttpStatus.UNPROCESSABLE_ENTITY),
    CODICE_CONTRATTO_PRESENTE("19", "id_appio con bilagr già associato", HttpStatus.UNPROCESSABLE_ENTITY),
    DB_INTERNAL_ERROR("67", "db internal error", HttpStatus.INTERNAL_SERVER_ERROR);

    @JsonValue
    @Getter
    private String code;

    @Getter
    private String description;

    @Getter
    private HttpStatus httpStatus;

    public static PpOnboardingBackResponseErrCode of(String code) {
        return Stream.of(PpOnboardingBackResponseErrCode.values())
                .filter(p -> StringUtils.equalsIgnoreCase(p.getCode(), code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("'" + code + "' cannot be map to 'PpOnboardingBackResponseErrCode'"));
    }
}
