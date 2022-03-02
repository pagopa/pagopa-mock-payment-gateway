package it.gov.pagopa.bpay.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionUpdateRequest {

    @JsonProperty("auth_outcome")
    private String authOutcome;

    @JsonProperty("auth_code")
    private String authCode;

}
