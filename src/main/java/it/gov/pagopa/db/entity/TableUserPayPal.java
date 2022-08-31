package it.gov.pagopa.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

@Table(name = "user_paypal")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableUserPayPal {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "id_appio")
    private String idAppIo;

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "paypal_email")
    private String paypalEmail;

    @Column(name = "paypal_id")
    private String paypalId;

    @Builder.Default
    @Column(name = "creation_date")
    private Instant creationDate=Instant.now();

    @Type(type= "org.hibernate.type.NumericBooleanType")
    private boolean deleted;

    @OneToMany(mappedBy = "tableUserPayPal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TablePaymentPayPal> paymentPayPal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private TableClient client;
}
