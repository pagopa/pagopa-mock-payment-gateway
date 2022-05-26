package it.gov.pagopa.postepay.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private String errorCode;
    private String description;
    private String message;

}
