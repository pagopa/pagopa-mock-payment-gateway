package it.gov.pagopa.postepay.repository;

import it.gov.pagopa.postepay.entity.PostePayPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostePayPaymentRepository extends JpaRepository<PostePayPayment, Long> {
    PostePayPayment findByPaymentId(String paymentId);
}
