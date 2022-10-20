package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XPayReportDetail {
    private String nome;
    private String cognome;
    private String mail;
    private BigInteger importo;
    private BigInteger importoRifiutato;
    private String divisa;
    private String stato;
    private String codiceTransazione;
    private String parametriAggiuntivi;
    private BigInteger controvaloreValuta;
    private Integer decimaliValuta;
    private BigInteger tassoCambio;
    private Integer codiceValuta;
    private Boolean flagValuta;
    private List<XPayReportOperations> operazioni;
}
