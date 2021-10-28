package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PpRefundDirectResponse extends PpDefaultResponse {

    public PpRefundDirectResponse(PpDefaultResponse ppDefaultResponse) {
        this.setPpDefaultErrorResponse(ppDefaultResponse);
    }
}
