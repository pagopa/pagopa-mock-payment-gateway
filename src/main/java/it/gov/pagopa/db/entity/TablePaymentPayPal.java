package it.gov.pagopa.db.entity;

import it.gov.pagopa.paypalpsp.dto.dtoenum.PpEsitoResponseCode;
import it.gov.pagopa.paypalpsp.dto.dtoenum.PpResponseErrCode;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "payment_paypal")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TablePaymentPayPal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal fee;

    private BigDecimal importo;

    @Column(name = "id_trs_appio")
    private String idTrsAppIo;

    @Column(name = "id_trs_paypal")
    private String idTrsPaypal;

    @Enumerated(EnumType.STRING)
    private PpEsitoResponseCode esito;

    @Column(name = "err_cod")
    @Enumerated(EnumType.STRING)
    private PpResponseErrCode errCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_paypal_id")
    private TableUserPayPal tableUserPayPal;

    @Enumerated(EnumType.STRING)
    private PpEsitoResponseCode esitoRefund;

    @Column(name = "err_cod_refund")
    @Enumerated(EnumType.STRING)
    private PpResponseErrCode errCodeRefund;

}
