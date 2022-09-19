package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpEsitoResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class PpDefaultResponse {
    private PpEsitoResponseCode esito = PpEsitoResponseCode.KO;

    @JsonProperty("err_cod")
    private PpResponseErrCode errCod;

    @JsonProperty("err_desc")
    private String errDesc;

    public PpDefaultResponse(PpEsitoResponseCode ppEsitoResponseCode) {
        this.esito = ppEsitoResponseCode;
    }

    public void setPpDefaultErrorResponse(PpDefaultResponse ppDefaultResponse) {
        this.setErrCod(ppDefaultResponse.getErrCod());
        this.setErrDesc(ppDefaultResponse.getErrDesc());
        this.setEsito(ppDefaultResponse.getEsito());
    }
}
