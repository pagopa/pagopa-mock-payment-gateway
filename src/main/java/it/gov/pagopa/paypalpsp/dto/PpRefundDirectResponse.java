package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PpRefundDirectResponse extends PpDefaultErrorResponse {

    public PpRefundDirectResponse(PpDefaultErrorResponse ppDefaultErrorResponse) {
        this.setPpDefaultErrorResponse(ppDefaultErrorResponse);
    }
}
