package it.gov.pagopa.transaction.controller;

import it.gov.pagopa.service.ConfigService;
import it.gov.pagopa.transaction.dto.TransactionStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static it.gov.pagopa.xpay.utils.XPayConstants.TRANSACTION_HTTP_CODE_RESPONSE;
import static it.gov.pagopa.xpay.utils.XPayConstants.TRANSACTION_STATUS_RESPONSE;

@RestController
@RequestMapping("/transactions/change")
public class TransactionSettingsController {
    @Autowired
    private ConfigService configService;

    private static final List<String> HTTP_STATUS_CODES = Arrays.asList("200", "400", "404", "500", "502", "505");

    @PostMapping("/auth-requests/outcome")
    public void changeTransactionAuthOutcome(@RequestParam(required = false) TransactionStatusEnum transactionStatus,
                                             @RequestParam(required = false) String httpStatus) {

        if(StringUtils.isNotBlank(httpStatus)) {
            if(!HTTP_STATUS_CODES.contains(httpStatus))
                throw new IllegalArgumentException("HttpStatus not valid");

            configService.updateConfigValue(TRANSACTION_HTTP_CODE_RESPONSE, httpStatus);
        }

        if(transactionStatus != null)
            configService.updateConfigValue(TRANSACTION_STATUS_RESPONSE, transactionStatus.toString());
    }
}
