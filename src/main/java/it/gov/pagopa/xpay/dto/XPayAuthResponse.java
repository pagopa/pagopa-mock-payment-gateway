package it.gov.pagopa.xpay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class XPayAuthResponse {
    private XPayOutcome esito;
    private String idOperazione;
    private Long timeStamp;
    private String html;
    private XPayError errore;
    private String mac;
}
