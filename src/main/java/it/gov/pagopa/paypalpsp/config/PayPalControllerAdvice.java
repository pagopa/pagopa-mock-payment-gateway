package it.gov.pagopa.paypalpsp.config;

import it.gov.pagopa.paypalpsp.dto.PpDefaultResponse;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import it.gov.pagopa.paypalpsp.mockcontroller.PayPalPspRestController;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@ControllerAdvice(assignableTypes = {PayPalPspRestController.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PayPalControllerAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex);
        PpResponseErrCode parametriNonValidi = PpResponseErrCode.PARAMETRI_NON_VALIDI;
        PpDefaultResponse ppErrorResponse = new PpDefaultResponse();
        ppErrorResponse.setErrCod(parametriNonValidi);
        ppErrorResponse.setErrDesc(parametriNonValidi.getDescription());

        return ResponseEntity.status(parametriNonValidi.getHttpStatus()).body(ppErrorResponse);
    }
}
