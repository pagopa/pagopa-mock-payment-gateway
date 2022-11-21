package it.gov.pagopa.vpos.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MethodData3Ds {
    private String threeDSMethodNotificationUrl;
    private String threeDSServerTransID;
}
