package it.gov.pagopa.paypalpsp.dto.dtoenum;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PpOnboardingCallResponseEsito {
    OK("1"),
    CANCEL("3"),
    KO("9");

    @JsonValue
    @Getter
    private String code;
}
