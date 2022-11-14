package it.gov.pagopa.vpos.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum VposHttpResponseEnum {
    OK("200"),
    NOT_FOUND("404"),
    SERVICE_UNAVAILABLE("503");

    @JsonValue
    @Getter
    private final String code;
}
