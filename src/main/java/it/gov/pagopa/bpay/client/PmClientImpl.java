package it.gov.pagopa.bpay.client;

import feign.*;
import feign.jackson.*;
import it.gov.pagopa.bpay.dto.*;

public class PmClientImpl {

    public PmClientImpl(String baseUrl) {
        pmClient = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(PmClient.class, baseUrl);
    }

    private final PmClient pmClient;

    public void updateTransaction(String transactionId, TransactionUpdateRequest request) {
        pmClient.updateTransaction(transactionId, request);
    }

}
