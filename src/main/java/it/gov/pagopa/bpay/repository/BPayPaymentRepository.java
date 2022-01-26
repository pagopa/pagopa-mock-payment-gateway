package it.gov.pagopa.bpay.repository;

import it.gov.pagopa.bpay.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface BPayPaymentRepository extends JpaRepository<BPayPayment, Long> {

    BPayPayment findByCorrelationId(String correlationId);

}
