package it.gov.pagopa.xpay.dto;

import org.springframework.http.HttpStatus;

public enum XPayErrorEnum {
    ERROR_1(1L, "Uno dei valori dei parametri del json in input non è corretto", HttpStatus.BAD_REQUEST),
    ERROR_2(2L, "Non è possibile trovare l’informazione richiesta", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_3(3L, "MAC errato", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_4(4L, "MAC non presente nella richiesta json", HttpStatus.BAD_REQUEST),
    ERROR_5(5L, "Sono trascorsi più di 5 minuti da quando il timestamp è stato generato", HttpStatus.REQUEST_TIMEOUT),
    ERROR_6(6L, "apiKey non contiene un alias valido", HttpStatus.BAD_REQUEST),
    ERROR_7(7L, "apiKey non contiene un alias valido", HttpStatus.BAD_REQUEST),
    ERROR_8(8L, "Contratto non valido", HttpStatus.BAD_REQUEST),
    ERROR_9(9L, "Transazione già presente", HttpStatus.BAD_REQUEST),
    ERROR_12(12L, "Gruppo non valido", HttpStatus.BAD_REQUEST),
    ERROR_13(13L, "La transazione non è stata trovata", HttpStatus.BAD_REQUEST),
    ERROR_14(14L, "La carta è scaduta", HttpStatus.BAD_REQUEST),
    ERROR_15(15L, "Brand carta non permesso", HttpStatus.BAD_REQUEST),
    ERROR_16(16L, "Valore non valido nello stato corrente", HttpStatus.BAD_REQUEST),
    ERROR_17(17L, "Importo operazione troppo alto", HttpStatus.BAD_REQUEST),
    ERROR_18(18L, "Numero tentativi di retry esauriti", HttpStatus.BAD_REQUEST),
    ERROR_19(19L, "Pagamento rifiutato. Verificare le note presenti in fondo alla pagina per verificare i possibili valori del campo \"messaggio\".", HttpStatus.BAD_REQUEST),
    ERROR_20(20L, "Autenticazione 3DS annullata", HttpStatus.UNAUTHORIZED),
    ERROR_21(21L, "Autenticazione 3DS fallita", HttpStatus.UNAUTHORIZED),
    ERROR_22(22L, "Carta non valida per l’addebito (scaduta o bloccata)", HttpStatus.BAD_REQUEST),
    ERROR_50(50L, "Impossibile calcolare il mac, nei caso in cui l’alias non sia valido o il json in ingresso non sia conforme a quello richiesto", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_96(96L, "In caso di esito KO con codice di errore 96, è possibile ritentare il pagamento riutilizzando lo stesso codice transazione e passando come parametro \"softDecline\" valorizzato a \"S\" nella creaNonce. Si riceverà in risposta il codice html che forzerà la SCA, in modo da ottenere un nuovo nonce da utilizzare nell'api pagaNonce",HttpStatus.BAD_REQUEST),
    ERROR_97(97L, "Errore generico", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_98(98L, "Metodo non ancora implementato", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_99(99L, "Operazione non permessa, il merchant non ha i requisiti per effettuare l’operazione richiesta", HttpStatus.UNAUTHORIZED),
    ERROR_100(100L, "Errore interno", HttpStatus.INTERNAL_SERVER_ERROR);

    private final Long codiceErrore;
    private final String descrizione;
    private final HttpStatus httpStatus;

    XPayErrorEnum(Long codiceErrore, String descrizione, HttpStatus httpStatus) {
        this.codiceErrore = codiceErrore;
        this.descrizione = descrizione;
        this.httpStatus = httpStatus;
    }

    public Long getCodiceErrore() {
        return codiceErrore;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
