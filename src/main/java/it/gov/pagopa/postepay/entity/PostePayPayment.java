package it.gov.pagopa.postepay.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "payment_postepay")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostePayPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_transaction_id")
    private String shopTransactionId;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "outcome")
    private String outcome;

    @Column(name = "is_refunded")
    private Boolean isRefunded;

}
