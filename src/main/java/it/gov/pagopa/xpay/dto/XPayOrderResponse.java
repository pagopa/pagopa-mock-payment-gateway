package it.gov.pagopa.xpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XPayOrderResponse {
    private Long timeStamp;
    private XPayOutcome esito;
    private String idOperazione;
    private String scadenza; //Formato: aaaamm
    private String mac;
    private XpayError errore;
    private XPayReport report;
}
