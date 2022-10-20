package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XPayReportOperations {
    private String tipoOperazione;
    private BigInteger importo;
    private String divisa;
    private String stato;
    private String dataOperazione; //Formato: gg/mm/aaaa
    private String utente;
    private String idContabParzialePayPal;
}
