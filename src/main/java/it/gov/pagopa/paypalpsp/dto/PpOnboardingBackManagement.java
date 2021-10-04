package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PpOnboardingBackManagement {
    @NotNull
    @JsonProperty("id_appio")
    private String idAppIo;

    @JsonProperty("err_code")
    private PpResponseErrCode errCode;

    @NotNull
    @JsonProperty("api_id")
    private ApiPaypalIdEnum apiId;

    @JsonFormat(pattern = "dd/MM/yyyy HH:ss:SSSS", timezone = "Europe/Rome", shape = JsonFormat.Shape.STRING)
    private Instant lastUpdateDate;
}
