package it.gov.pagopa.vpos.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ResponseAccountingRevertStatusEnum {
    SUCCESS("00"),
    INCORRECT_MESSAGE_FORMAT_MISSING_OR_INCORRECT_FIELD("03");

    @JsonValue
    @Getter
    private final String code;
}
