package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XPayOrderRequest {
    @NotBlank
    private String apiKey;
    @NotBlank
    private String codiceTransazione;
    @NotBlank
    private String timeStamp;
    @NotBlank
    private String mac;
}
