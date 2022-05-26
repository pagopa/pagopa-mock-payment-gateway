package it.gov.pagopa.postepay.repository;

import it.gov.pagopa.postepay.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface PostePayPaymentRepository extends JpaRepository<PostePayPayment, Long> {

}
