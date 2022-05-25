package it.gov.pagopa.postepay.dto;

import lombok.*;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {

    @NotEmpty
    private String shopId;
    @NotEmpty
    private String shopTransactionId;
    @NotEmpty
    private String amount;
    private String description;
    @NotEmpty
    private String currency;
    private String buyerName;
    private String buyerEmail;
    @NotNull
    private PaymentChannelEnum paymentChannel;
    @NotNull
    private AuthTypeEnum authType;
    @NotNull
    private ResponseURLs responseURLs;

}
