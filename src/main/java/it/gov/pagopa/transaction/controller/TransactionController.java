package it.gov.pagopa.transaction.controller;

import it.gov.pagopa.transaction.dto.UpdateAuthRequest;
import it.gov.pagopa.transaction.service.UpdateTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private UpdateTransactionService updateTransactionService;

    @PatchMapping("/{transactionId}/auth-requests")
    public ResponseEntity<Object> updateTransactionAuth(@Validated @RequestBody UpdateAuthRequest input,
                                                        @PathVariable String transactionId) {

        return updateTransactionService.getTransactionAuthMock(input, transactionId);
    }
}
