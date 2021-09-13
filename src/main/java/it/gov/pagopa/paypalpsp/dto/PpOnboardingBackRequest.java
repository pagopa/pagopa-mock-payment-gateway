package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PpOnboardingBackRequest {
    @NotNull
    @JsonProperty("url_return")
    private String urlReturn;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Europe/Rome", pattern = "yyyyMMddHHmmss")
    private Instant timestamp;

    @NotNull
    @JsonProperty("id_appio")
    private String idAppIo;

}
