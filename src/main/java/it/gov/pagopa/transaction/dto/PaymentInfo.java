package it.gov.pagopa.transaction.dto;

import lombok.Data;

@Data
public class PaymentInfo {
    private String paymentToken;
    private String rptId;
    private String reason;
    private String authToken;
    private Integer amount;
}
