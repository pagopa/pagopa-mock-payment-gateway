package it.gov.pagopa.bpay.dto;

import lombok.*;

@AllArgsConstructor
@Getter
public enum EsitoEnum {

    OK(true, false, "0", "Esito positivo"),
    PAYMENT_NOT_FOUND(false, false, "01017", "Pagamento non trovato"),
    GENERIC_ERROR(false, false, "-1", "Errore generico"),
    NO_ACTIVE_CONTACT_FOUND(false, false, "-2", "Non è stato trovato un contatto attivo associato al buyer selezionato"),
    NO_ACTIVE_BUYER_FOUND(false, false, "-3", "Nessun buyer attivo trovato"),
    BPAY_NOT_ACTIVE(false, false, "-4", "Il buyer non ha un servizio BANCOMAT Pay attivo"),
    NO_STORE_FOUND(false, false, "-5", "Nessun negozio online attivo trovato per il merchant"),
    NO_SERVICE_FOUND(false, false, "-6", "Il merchant non ha un servizio attivo"),
    BANK_NOT_ENABLED(false, false, "-89", "Operazione chiusa con esito negativo. Numero attivo su Banca non abilitata alle funzionalità di pagamento"),
    MERCHANT_NOT_FOUND(false, false, "-1002", "Merchant non trovato"),
    INVALID_AMOUNT(false, false, "01024", "L'importo del pagamento non è valido"),
    MERCHANT_BLOCKED(false, false, "01039", "Merchant bloccato per i pagamenti dello specifico buyer"),
    IDPAGOPA_OR_IDPSP_ALREADY_CREATED(false, false, "01065", "Errore l'idPagoPa o l'idPSP sono già censiti"),
    EMPTY_FIELD(false, false, "02000", "Il campo {0} non e' valorizzato"),
    MERCHANT_NOT_ENABLED(false, false, "03004", "Merchant non abilitato alla categoria di pagamento"),
    OPERATION_NOT_ALLOWED(false, false, "03016", "Operazione non consentita"),
    SERVICE_NOT_ENABLED(false, false, "03020", "Il servizio del negozio non è abilitato a censire questo pagamento");

    private final boolean esito;
    private final boolean avvertenza;
    private final String codice;
    private final String messaggio;

    public static EsitoEnum fromCode(String code) {
        EsitoEnum[] values = EsitoEnum.values();
        for (EsitoEnum item : values) {
            if (item.codice.equals(code)) {
                return item;
            }
        }
        throw new IllegalArgumentException("Cannot find code " + code);
    }

}
