package it.gov.pagopa.xpay.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;

@Table(name = "payment_xpay")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class XPayPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "id_operazione")
    private String idOperazione;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "url_risposta")
    private String urlRisposta;

    @Column(name = "codice_transazione")
    private String codiceTransazione;

    @Column(name = "importo")
    private BigInteger importo;

    @Column(name = "divisa")
    private Long divisa;

    @Column(name = "pan")
    private String pan;

    @Column(name = "scadenza")
    private String scadenza;

    @Column(name = "cvv")
    private String cvv;

    @Column(name = "timestamp_request")
    private String timeStamp_request;

    @Column(name = "timestamp_response")
    private Long  timeStamp_response;

    @Column(name = "mac")
    private String mac;


}
