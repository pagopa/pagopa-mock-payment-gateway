package it.gov.pagopa.postepay.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailsPaymentResponse {
    private String shopId;
    private String shopTransactionId;
    private String paymentID;
    private String result;
    private String authNumber;
    private String amount;
    private String description;
    private String currency;
    private String buyerName;
    private String buyerEmail;
    private String paymentChannel;
    private String authType;
    private String status;
    private String refundedAmount;
}
