package it.gov.pagopa.xpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XPayAuthResponse {

    private EsitoXpay esito;
    private String idOperazione;
    private Long timeStamp;
    private String html;
    private XpayError errore;
    private String mac;
    private String xpayNonce;
}
