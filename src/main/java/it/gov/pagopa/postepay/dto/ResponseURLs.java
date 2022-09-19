package it.gov.pagopa.postepay.dto;

import lombok.*;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseURLs {

    @NotEmpty
    private String responseUrlOk;
    @NotEmpty
    private String responseUrlKo;
    @NotEmpty
    private String serverNotificationUrl;

}
