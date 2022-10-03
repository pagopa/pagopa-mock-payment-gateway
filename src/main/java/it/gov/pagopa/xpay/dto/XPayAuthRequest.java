package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XPayAuthRequest {

    @NotBlank
    private String apiKey;
    @NotBlank
    private String urlRisposta;
    @NotBlank
    private String codiceTransazione;
    @NotNull
    private BigInteger importo;
    @NotNull
    private Long divisa;
    @NotBlank
    private String pan;
    @NotBlank
    private String scadenza;
    @NotBlank
    private String cvv;
    @NotBlank
    private String timeStamp;
    @NotBlank
    private String mac;
}
