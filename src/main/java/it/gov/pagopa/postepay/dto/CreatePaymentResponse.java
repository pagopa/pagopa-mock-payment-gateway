package it.gov.pagopa.postepay.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentResponse {

    private String paymentID;
    private String userRedirectURL;

}
