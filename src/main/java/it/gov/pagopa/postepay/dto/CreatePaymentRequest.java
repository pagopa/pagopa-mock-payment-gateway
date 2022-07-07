package it.gov.pagopa.postepay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {

    @NotEmpty
    private String merchantId;

    @NotEmpty
    private String shopId;

    @NotEmpty
    private String shopTransactionId;

    @NotEmpty
    private String amount;

    @NotEmpty
    private String currency;

    @NotNull
    private PaymentChannelEnum paymentChannel;

    @NotNull
    private AuthTypeEnum authType;

    @NotNull
    private ResponseURLs responseURLs;

    private String buyerName;
    private String buyerEmail;
    private String description;
}
