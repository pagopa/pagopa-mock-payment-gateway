package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpEsitoResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PpDefaultErrorResponse {
    private PpEsitoResponseCode esito = PpEsitoResponseCode.KO;

    @JsonProperty("err_cod")
    private PpResponseErrCode errCod;

    @JsonProperty("err_desc")
    private String errDesc;

}
