package it.gov.pagopa.vpos.dto.request;

import lombok.Data;

@Data
public class SaveResponseChallenge3Ds2 {
    private String outcome;
    private String threeDSServerTransID;
}
