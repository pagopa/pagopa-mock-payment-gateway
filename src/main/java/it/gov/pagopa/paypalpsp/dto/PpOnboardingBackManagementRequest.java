package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PpOnboardingBackManagementRequest extends PPPayPalIdAppIoRequest{
    @JsonProperty("err_code")
    private PpResponseErrCode errCode;

    @NotNull
    @JsonProperty("api_id")
    private ApiPaypalIdEnum apiId;

}
