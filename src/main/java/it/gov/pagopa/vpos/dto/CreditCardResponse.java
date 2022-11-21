package it.gov.pagopa.vpos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardResponse {
    String cardNumber;
    String urlImg;
}
