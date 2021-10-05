package it.gov.pagopa.paypalpsp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.gov.pagopa.db.entityenum.ApiPaypalIdEnum;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PpResponseErrorInfo {

    private String code;

    private String description;

    private String httpStatus;

    private Integer httpCode;

    private List<ApiPaypalIdEnum> allowed;

    public PpResponseErrorInfo(PpResponseErrCode ppResponseErrCode) {
        this.code = ppResponseErrCode.getCode();
        this.description = ppResponseErrCode.getDescription();
        this.allowed = ppResponseErrCode.getAllowed();
        HttpStatus httpStatusEnum = ppResponseErrCode.getHttpStatus();
        if (httpStatusEnum != null) {
            this.httpStatus = httpStatusEnum.getReasonPhrase();
            this.httpCode = httpStatusEnum.value();
        }
    }
}
