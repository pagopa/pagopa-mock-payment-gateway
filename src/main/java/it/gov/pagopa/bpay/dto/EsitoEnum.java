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

    public static EsitoEnum fromCode(String code) throws Exception {
        EsitoEnum[] values = EsitoEnum.values();
        for (EsitoEnum item : values) {
            if (item.codice.equals(code)) {
                return item;
            }
        }
        throw new Exception("Cannot find code " + code);
    }

}
