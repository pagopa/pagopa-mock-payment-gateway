package it.gov.pagopa.postepay.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailsPaymentRequest {

    @NotEmpty
    private String shopId;
    @NotEmpty
    private String shopTransactionId;
    @NotEmpty
    private String paymentID;

}
