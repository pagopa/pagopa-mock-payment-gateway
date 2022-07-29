package it.gov.pagopa.postepay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingResponse {
    private String paymentID;
    private String userRedirectURL;
}
