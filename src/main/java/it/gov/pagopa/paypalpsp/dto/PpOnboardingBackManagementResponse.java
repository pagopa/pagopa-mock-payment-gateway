package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class PpOnboardingBackManagementResponse {
    @JsonProperty("id_appio")
    private String idAppIo;

    @JsonProperty("api_id")
    private ApiPaypalIdEnum apiId;

    @JsonFormat(pattern = "dd/MM/yyyy HH:ss:SSSS", timezone = "Europe/Rome", shape = JsonFormat.Shape.STRING)
    private Instant lastUpdateDate;

    @JsonProperty("err_info")
    private PpResponseErrorInfo errorInfo;

    public void setErrorInfo(PpResponseErrCode ppResponseErrCode) {
        if (ppResponseErrCode != null) {
            this.errorInfo = new PpResponseErrorInfo(ppResponseErrCode);
        }
    }
}
