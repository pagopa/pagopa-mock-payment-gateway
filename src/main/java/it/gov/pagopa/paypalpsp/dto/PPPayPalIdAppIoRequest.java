package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PPPayPalIdAppIoRequest {
    @NotNull
    @JsonProperty("id_appio")
    private String idAppIo;

}
