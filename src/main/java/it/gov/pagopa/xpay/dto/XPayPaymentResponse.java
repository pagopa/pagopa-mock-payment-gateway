package it.gov.pagopa.xpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XPayPaymentResponse {
    private Long timeStamp;
    private XPayOutcome esito;
    private String idOperazione;
    private String codiceAutorizzazione;
    private String codiceConvenzione;
    private String data;
    private String nazione;
    private String regione;
    private String brand;
    private String tipoProdotto;
    private String tipoTransazione;
    private String mac;
    private String ppo;
    private XPayError errore;
    private XPayAdditionalParameters parametriAggiuntivi;
}
