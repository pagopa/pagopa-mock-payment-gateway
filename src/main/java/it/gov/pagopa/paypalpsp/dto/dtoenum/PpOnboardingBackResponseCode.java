package it.gov.pagopa.paypalpsp.dto.dtoenum;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PpOnboardingBackResponseCode {
    OK("1"),
    KO("9");

    @JsonValue
    @Getter
    private String code;
}
