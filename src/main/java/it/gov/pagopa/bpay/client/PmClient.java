package it.gov.pagopa.bpay.client;

import feign.*;
import it.gov.pagopa.bpay.dto.*;

@Headers({"Content-Type: application/json"})
public interface PmClient {

    @RequestLine("PUT /payment-gateway/request-payments/bancomatpay")
    void updateTransaction(TransactionUpdateRequest transactionUpdateRequest);

}
