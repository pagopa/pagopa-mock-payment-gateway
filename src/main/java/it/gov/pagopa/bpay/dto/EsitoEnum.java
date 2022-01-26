package it.gov.pagopa.bpay.dto;

import lombok.*;

@AllArgsConstructor
@Getter
public enum EsitoEnum {

    OK(true, false, "0", "Esito positivo"),
    PAYMENT_NOT_FOUND(false, false, "01017", "Pagamento non trovato");

    private final boolean esito;
    private final boolean avvertenza;
    private final String codice;
    private final String messaggio;

}
