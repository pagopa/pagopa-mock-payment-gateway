package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XPayAuthRequest {

    private String apiKey;
    private String urlRisposta;
    private String codiceTransazione;
    private BigInteger importo;
    private Long divisa;
    private String pan;
    private String scadenza;
    private String cvv;
    private String timeStamp;
    private String mac;
    private String numeroContratto;
}
