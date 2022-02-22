package it.gov.pagopa.bpay.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionUpdateRequest {

    private String end2EndId;

}
