package it.gov.pagopa.bpay.entity;

import lombok.*;

import javax.persistence.*;
import java.math.*;

@Table(name = "payment_bpay")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class BPayPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "id_pagopa")
    private String idPagoPa;

    @Column(name = "id_psp")
    private String idPsp;

    @Column(name = "outcome")
    private String outcome;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "refund_outcome")
    private String refundOutcome;

    @Column(name = "client_hostname")
    private String clientHostname;

}
