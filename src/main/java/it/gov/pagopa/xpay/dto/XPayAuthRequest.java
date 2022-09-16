package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.Valid;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class XPayAuthRequest {

    @NonNull
    private String apiKey;
    @NonNull
    private String urlRisposta;
    @NonNull
    private String codiceTransazione;
    @NonNull
    private BigInteger importo;
    @NonNull
    private Long divisa;
    @NonNull
    private String pan;
    @NonNull
    private String scadenza;
    @NonNull
    private String cvv;
    @NonNull
    private String timeStamp;
    @NonNull
    private String mac;
}
