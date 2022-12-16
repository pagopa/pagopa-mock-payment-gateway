package it.gov.pagopa.transaction.service;

import it.gov.pagopa.db.repository.TableConfigRepository;
import it.gov.pagopa.transaction.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static it.gov.pagopa.xpay.utils.XPayConstants.TRANSACTION_HTTP_CODE_RESPONSE;
import static it.gov.pagopa.xpay.utils.XPayConstants.TRANSACTION_STATUS_RESPONSE;

@Service
public class UpdateTransactionService {
    @Autowired
    private TableConfigRepository configRepository;

    private Integer httpResponse;
    private String statusResponse;

    private void refreshConfigs() {
        httpResponse = Integer.parseInt(configRepository.findByPropertyKey(TRANSACTION_HTTP_CODE_RESPONSE).getPropertyValue());
        statusResponse = configRepository.findByPropertyKey(TRANSACTION_STATUS_RESPONSE).getPropertyValue();
    }

    public ResponseEntity<Object> getTransactionAuthMock(UpdateAuthRequest input, String transactionId) {
        refreshConfigs();

        if(httpResponse == 200) {
            TransactionInfo transactionInfo = createTransactionInfo(transactionId);
            return ResponseEntity.status(httpResponse).body(transactionInfo);
        } else {
            ProblemJson problemJson = new ProblemJson();
            String problemUri = "https://api.dev.platform.pagopa.it/mock-payment-gateway/api/transactions/" + transactionId + "/auth-requests";
            problemJson.setType(problemUri);
            problemJson.setInstance(problemUri);
            problemJson.setTitle("Title error");
            problemJson.setDetail("There was an error processing the request");
            problemJson.setStatus(httpResponse);

            return ResponseEntity.status(httpResponse).body(problemJson);
        }
    }

    private TransactionInfo createTransactionInfo(String transactionId) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setAuthToken("3edf460c5f7aty5");
        paymentInfo.setRptId("77777777777302012387654312384");
        paymentInfo.setPaymentToken("1a6dfg4g9d2d4aaaa");
        paymentInfo.setAmount(5000);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setTransactionId(transactionId);
        transactionInfo.setStatus(TransactionStatusEnum.valueOf(statusResponse));
        transactionInfo.addPaymentInfo(paymentInfo);

        return transactionInfo;
    }
}
