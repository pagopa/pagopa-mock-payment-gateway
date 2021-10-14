package it.gov.pagopa.paypalpsp.util;

import it.gov.pagopa.db.entity.TableUserPayPal;
import it.gov.pagopa.paypalpsp.dto.*;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpEsitoResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PayPalCreateResponse {
    public static ResponseEntity<PpPayDirectResponse> createSuccessPaymentResponse(PpPayDirectRequest ppPayDirectRequest) {
        PpPayDirectResponse response = new PpPayDirectResponse();
        response.setEsito(PpEsitoResponseCode.OK);
        response.setIdTrsPaypal(StringUtils.leftPad(ppPayDirectRequest.getIdTrsAppIo(), 20, "0"));
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<PpRefundDirectResponse> createSuccessRefundResponse() {
        PpRefundDirectResponse response = new PpRefundDirectResponse();
        response.setEsito(PpEsitoResponseCode.OK);
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<PpOnboardingBackResponse> manageErrorResponseAlreadyOnboarded(TableUserPayPal tableUserPayPal) {
        PpResponseErrCode codiceContrattoPresente = PpResponseErrCode.CODICE_CONTRATTO_PRESENTE;
        PpDefaultErrorResponse onboardingBackResponseResponseEntity = manageErrorResponse(codiceContrattoPresente);
        PpDefaultErrorResponse ppOnboardingBackResponse = Objects.requireNonNull(onboardingBackResponseResponseEntity);
        PpOnboardingBackResponse ppOnboardingBackResponse1 = new PpOnboardingBackResponse();
        ppOnboardingBackResponse1.setEmailPp(PaypalUtils.obfuscateEmail(tableUserPayPal.getPaypalEmail()));
        ppOnboardingBackResponse1.setIdPp(tableUserPayPal.getPaypalId());
        ppOnboardingBackResponse1.setPpDefaultErrorResponse(ppOnboardingBackResponse);
        return ResponseEntity.status(codiceContrattoPresente.getHttpStatus()).body(ppOnboardingBackResponse1);
    }

    private static PpDefaultErrorResponse manageErrorResponse(PpResponseErrCode errCode) {
        PpDefaultErrorResponse build = new PpOnboardingBackResponse();
        build.setEsito(PpEsitoResponseCode.KO);
        build.setErrCod(errCode);
        build.setErrDesc(errCode.getDescription());
        return build;
    }

    public static ResponseEntity<PpOnboardingBackResponse> createResponseErrorOnboarding(PpResponseErrCode ppResponseErrCode) {
        PpDefaultErrorResponse ppDefaultErrorResponse = manageErrorResponse(ppResponseErrCode);
        return ResponseEntity.status(ppResponseErrCode.getHttpStatus()).body(new PpOnboardingBackResponse(ppDefaultErrorResponse));
    }

    public static ResponseEntity<PpPayDirectResponse> createResponseErrorPayment(PpResponseErrCode ppResponseErrCode) {
        PpDefaultErrorResponse ppDefaultErrorResponse = manageErrorResponse(ppResponseErrCode);
        return ResponseEntity.status(ppResponseErrCode.getHttpStatus()).body(new PpPayDirectResponse(ppDefaultErrorResponse));
    }

    public static ResponseEntity<PpRefundDirectResponse> createRefundResponseError(PpResponseErrCode ppResponseErrCode) {
        PpDefaultErrorResponse ppDefaultErrorResponse = manageErrorResponse(ppResponseErrCode);
        return ResponseEntity.status(ppResponseErrCode.getHttpStatus()).body(new PpRefundDirectResponse(ppDefaultErrorResponse));
    }

}
