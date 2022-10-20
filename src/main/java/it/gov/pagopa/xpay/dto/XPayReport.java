package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XPayReport {
    private String numeroMerchant;
    private String codiceTransazione;
    private BigInteger importo;
    private String divisa;
    private String codiceAutorizzazione;
    private String brand;
    private String tipoPagamento;
    private String tipoTransazione;
    private String nazione;
    private String tipoProdotto;
    private String pan;
    private String parametri;
    private String stato;
    private String dataTransazione; //Formato: gg/mm/aaaa hh:mm:ss
    private String mail;
    private XPayReportDetail dettaglio;
}
