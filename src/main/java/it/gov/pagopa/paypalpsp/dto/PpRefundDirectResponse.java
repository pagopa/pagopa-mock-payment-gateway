package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpEsitoResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PpRefundDirectResponse extends PpDefaultErrorResponse {
    private PpEsitoResponseCode esito;

    @JsonProperty("err_cod")
    private PpResponseErrCode errCod;

    @JsonProperty("err_desc")
    private String errDesc;

    public PpRefundDirectResponse(PpDefaultErrorResponse ppDefaultErrorResponse) {
        this.setPpDefaultErrorResponse(ppDefaultErrorResponse);
    }
}
