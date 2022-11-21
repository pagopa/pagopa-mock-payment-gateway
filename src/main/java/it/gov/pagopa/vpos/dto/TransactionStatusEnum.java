package it.gov.pagopa.vpos.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TransactionStatusEnum {
    AUTH_GRANTED_BOOKABLE("00"),
    AUTH_DENIED("01"),
    AUTH_BOOKED_TO_BE_PROCESSED_BY_CLEARING("02"),
    AUTH_BOOKED_PROCESSED_BY_CLEARING("03"),
    AUTH_REVERSED("04"),
    AUTH_TO_BE_REVERSED_DUE_TO_ERROR("21"),
    AUTH_UNDERWAY("99");

    @JsonValue
    @Getter
    private final String code;
}
