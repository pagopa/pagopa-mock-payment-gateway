package it.gov.pagopa.bpay.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallBPayRequest {

    private String request;
    private String outcome;
    private boolean timeout;

}
