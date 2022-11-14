package it.gov.pagopa.vpos.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Response3Ds2Step0Enum {
    SUCCESS("00"),
    REQREFNUM_DUPLICATED_OR_INCORRECT("02"),
    INCORRECT_MESSAGE_FORMAT_MISSING_OR_INCORRECT_FIELD("03"),
    INCORRECT_API_AUTHENTICATION_INCORRECT_MAC("04"),
    UNFORESEEN_ERROR_DURING_PROCESSING_OF_REQUEST("06"),
    THE_CARD_IS_VBV_ENABLED("20"),
    METHOD("25"),
    A_CHALLENGE_FLOW("26"),
    EMPTY_XML_OR_MISSING_DATA_PARAMETER("40"),
    XML_NOT_PARSABLE("41"),
    INSTALLMENTS_NOT_AVAILABLE("50"),
    INSTALLMENT_NUMBER_OUT_OF_BOUNDS_CLIENT_SIDE("51"),
    TRANSACTION_FAILED_SEE_SPECIFIC_OUTCOME("99");

    @JsonValue
    @Getter
    private final String code;
}

