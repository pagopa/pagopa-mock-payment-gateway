package it.gov.pagopa.xpay.repository;

import it.gov.pagopa.xpay.entity.XPayPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XPayRepository extends JpaRepository<XPayPayment, Long> {
}
