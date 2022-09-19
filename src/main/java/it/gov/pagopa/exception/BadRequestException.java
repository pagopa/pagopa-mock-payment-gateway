package it.gov.pagopa.exception;

public class BadRequestException extends Exception {
    public BadRequestException(String msg) {
        super(msg);
    }
}
