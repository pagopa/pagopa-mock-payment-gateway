package it.gov.pagopa.vpos.dto;

import it.gov.pagopa.vpos.dto.response.*;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangeVposOutcome {
    @NotNull
    private Method3Ds2ResponseEnum method3dsOutcome;

    @NotNull
    private Response3Ds2Step0Enum step0Outcome;

    @NotNull
    private Response3Ds2Step1Enum step1Outcome;
    @NotNull
    private Response3Ds2Step2Enum step2Outcome;

    @NotNull
    private ResponseOrderStatusEnum orderStatusOutcome;

    @NotNull
    private TransactionStatusEnum transactionStatusOutcome;

    @NotNull
    private VposHttpResponseEnum httpOutcome;

    private ResponseAccountingRevertStatusEnum accountingOutcome;

    private ResponseAccountingRevertStatusEnum revertOutcome;
}
