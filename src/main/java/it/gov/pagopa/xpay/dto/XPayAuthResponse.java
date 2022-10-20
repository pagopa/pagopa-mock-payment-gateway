package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XPayAuthResponse {
    private XPayOutcome esito;
    private String idOperazione;
    private Long timeStamp;
    private String html;
    private XPayError errore;
    private String mac;
}
