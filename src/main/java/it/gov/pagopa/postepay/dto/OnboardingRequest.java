package it.gov.pagopa.postepay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingRequest {
    @NotEmpty
    private String merchantId;

    @NotEmpty
    private String shopId;

    @NotEmpty
    private String onboardingTransactionId;

    @NotNull
    private PaymentChannelEnum paymentChannel;

    @NotNull
    private ResponseURLs responseURLs;
}
