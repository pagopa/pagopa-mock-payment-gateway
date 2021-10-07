package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PpPayDirectResponse extends PpDefaultErrorResponse {

    @JsonProperty("id_trs_paypal")
    private String idTrsPaypal;

    public PpPayDirectResponse(PpDefaultErrorResponse ppDefaultErrorResponse) {
        this.setPpDefaultErrorResponse(ppDefaultErrorResponse);
    }
}
