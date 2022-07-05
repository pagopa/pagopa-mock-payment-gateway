package it.gov.pagopa.postepay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundPaymentRequest {
    private String merchantId;
    private String shopId;
    private String shopTransactionId;
    private String amount;
    private String reason;
    private String currency;
    private String paymentID;
    private String authNumber;
}
