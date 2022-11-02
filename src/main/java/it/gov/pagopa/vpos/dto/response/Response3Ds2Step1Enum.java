package it.gov.pagopa.vpos.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Response3Ds2Step1Enum {
    SUCCESS("00"),
    REQREFNUM_DUPLICATED_OR_INCORRECT("02"),
    INCORRECT_MESSAGE_FORMAT_MISSING_OR_INCORRECT_FIELD("03"),
    INCORRECT_API_AUTHENTICATION_INCORRECT_MAC("04"),
    UNFORESEEN_ERROR_DURING_PROCESSING_OF_REQUEST("06"),
    THREEDSTRANSID_NOT_FOUND("07"),
    MAXIMUM_TIME_LIMIT_FOR_FORWARDING_THE_VBV("21"),
    A_CHALLENGE_FLOW("26"),
    EMPTY_XML_OR_MISSING_DATA_PARAMETER("40"),
    XML_NOT_PARSABLE("41"),
    TRANSACTION_FAILED_SEE_SPECIFIC_OUTCOME("99");

    @JsonValue
    @Getter
    private String code;
}
