package it.gov.pagopa.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Table(name = "user_paypal")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableUserPayPal {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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

    private boolean deleted;
}
