package it.gov.pagopa.transaction.dto;

import lombok.Data;

@Data
public class ProblemJson {
    private String type;
    private String title;
    private String detail;
    private String instance;
    private Integer status;
}
