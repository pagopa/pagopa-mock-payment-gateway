package it.gov.pagopa.bpay.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallBPayRequest {

    private String request;
    private String outcome = EsitoEnum.OK.getCodice();
    private boolean timeout;

    public void reset() {
        request = null;
        outcome = EsitoEnum.OK.getCodice();
        timeout = false;
    }

}
