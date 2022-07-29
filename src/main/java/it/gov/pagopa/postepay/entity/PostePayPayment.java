package it.gov.pagopa.postepay.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

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

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "shop_id")
    private String shopId;

    @Column(name = "shop_transaction_id")
    private String shopTransactionId;

    @Column(name = "onboarding_transaction_id")
    private String onboardingTransactionId;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "outcome")
    private String outcome;

    @Column(name = "is_onboarding")
    private boolean isOnboarding;

    @Column(name = "is_refunded")
    private boolean isRefunded;

}
