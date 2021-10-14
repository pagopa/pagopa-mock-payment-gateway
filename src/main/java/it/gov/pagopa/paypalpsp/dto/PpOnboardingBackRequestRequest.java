package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PpOnboardingBackRequestRequest extends PPPayPalIdAppIoRequest {
    @NotNull
    @JsonProperty("url_return")
    private String urlReturn;

}
