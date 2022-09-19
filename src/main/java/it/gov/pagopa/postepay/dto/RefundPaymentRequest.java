package it.gov.pagopa.postepay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundPaymentRequest {
    @NotEmpty
    private String merchantId;
    @NotEmpty
    private String shopId;
    @NotEmpty
    private String shopTransactionId;
    @NotEmpty
    private String currency;
    @NotEmpty
    private String paymentID;
    @NotEmpty
    private String authNumber;

    private String amount;
    private String reason;
}
