package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpOnboardingBackResponseErrCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PpOnboardingBackResponse {
    private PpOnboardingBackResponseCode esito;

    @JsonProperty("url_to_call")
    private String urlToCall;

    @JsonProperty("id_pp")
    private String idPp;

    @JsonProperty("email_pp")
    private String emailPp;

    @JsonProperty("err_code")
    private PpOnboardingBackResponseErrCode errCode;

    @JsonProperty("err_desc")
    private String errDesc;
}
