package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PpRefundDirectRequest {

    @NotNull
    @JsonProperty("id_trs_appio")
    private String idTrsAppIo;

}
