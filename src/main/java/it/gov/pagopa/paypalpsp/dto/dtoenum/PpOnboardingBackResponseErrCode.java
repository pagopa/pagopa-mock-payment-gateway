package it.gov.pagopa.paypalpsp.dto.dtoenum;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@AllArgsConstructor
public enum PpOnboardingBackResponseErrCode {
    INPUT_NON_JSON("9"),
    PARAMETRI_NON_VALIDI("11"),
    AUTORIZZAZIONE_NEGATA("13"),
    AUTORIZZAZIONE_IP_NEGATA("15"),
    CODICE_CONTRATTO_PRESENTE("19"),
    DB_INTERNAL_ERROR("67");

    @JsonValue
    @Getter
    private String code;

    public static PpOnboardingBackResponseErrCode of(String code) {
        return Stream.of(PpOnboardingBackResponseErrCode.values())
                .filter(p -> StringUtils.equalsIgnoreCase(p.getCode(), code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("'" + code + "' cannot be map to 'PpOnboardingBackResponseErrCode'"));
    }
}
