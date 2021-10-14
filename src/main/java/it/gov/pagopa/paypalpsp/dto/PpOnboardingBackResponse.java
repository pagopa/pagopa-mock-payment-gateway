package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class PpOnboardingBackResponse extends PpDefaultResponse {
    @JsonProperty("url_to_call")
    private String urlToCall;

    @JsonProperty("id_pp")
    private String idPp;

    @JsonProperty("email_pp")
    private String emailPp;

    public PpOnboardingBackResponse(PpDefaultResponse ppDefaultResponse) {
        this.setPpDefaultErrorResponse(ppDefaultResponse);
    }

}
