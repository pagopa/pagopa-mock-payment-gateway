package it.gov.pagopa.transaction.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateAuthRequest {
    @NotNull
    private AuthResultEnum authorizationResult;

    @NotBlank
    private String timestampOperation; //2022-02-11T13:00:00+01:00

    private String authorizationCode;
}
