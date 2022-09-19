package it.gov.pagopa.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
public class DefaultError {
    @JsonFormat(pattern = "dd/MM/yyyy HH:ss:SSSS", timezone = "Europe/Rome", shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    private HttpStatus status;
    private String error;
    private String stackTrace;

}
