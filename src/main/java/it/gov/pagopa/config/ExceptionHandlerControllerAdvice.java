package it.gov.pagopa.config;

import it.gov.pagopa.exception.BadRequestException;
import it.gov.pagopa.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@Log4j2
@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity springHandleResponseStatusNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity springHandleDefaultException(Exception ex) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error(ex);
        DefaultError defaultError = new DefaultError();
        defaultError.setError(ExceptionUtils.getMessage(ex));
        defaultError.setStackTrace(ExceptionUtils.getStackTrace(ex));
        defaultError.setStatus(internalServerError);
        defaultError.setTimestamp(Instant.now());
        return ResponseEntity.status(internalServerError).body(defaultError);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity customBadRequest(BadRequestException ex) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        log.error(ex);
        DefaultError defaultError = new DefaultError();
        defaultError.setError(ExceptionUtils.getMessage(ex));
        defaultError.setStackTrace(ExceptionUtils.getStackTrace(ex));
        defaultError.setStatus(httpStatus);
        defaultError.setTimestamp(Instant.now());
        return ResponseEntity.status(httpStatus).body(defaultError);
    }

}
